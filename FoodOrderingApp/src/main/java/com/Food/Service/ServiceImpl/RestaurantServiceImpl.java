package com.Food.Service.ServiceImpl;

import com.Food.Model.Restaurant;
import com.Food.Model.User;
import com.Food.Repository.IAddressRepository;
import com.Food.Repository.IRestaurantRepository;
import com.Food.Repository.IUserRepository;
import com.Food.Service.IResturantService;
import com.Food.Service.IUserServices;
import com.Food.dto.ResturantDto;
import com.Food.request.CreateRestaurantRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements IResturantService {

    //Constructor Injection
    private final IUserServices userService;
    private final IAddressRepository addressRepository;
    private final IRestaurantRepository restaurantRepository;
    private final ModelMapper mapper;
    private final IUserRepository userRepository;

    @Override
    @Transactional
    public Restaurant createRestaurant(CreateRestaurantRequest req, User user) {
        Restaurant restaurant = Restaurant.builder()
                .name(req.getName())
                .description(req.getDescription())
                .cusineType(req.getCusineType())
                .address(req.getAddress())
                .contactInformation(req.getContactInformation())
                .openingHours(req.getOpeningHours())
                .images(req.getImages())
                .owner(user)
                .registrationDate(LocalDateTime.now())
                .build();
        return restaurantRepository.save(restaurant);
    }

    @Override
    @Transactional
    public Restaurant updateRestaurant(Long Restaurantid, CreateRestaurantRequest updatedRestaurant) throws Exception {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Restaurant restaurant = findRestaurantById(Restaurantid);
        User currentUser = userService.findByUsername(username);
        if (!Objects.equals(restaurant.getOwner().getId(), currentUser.getId())) {
            throw new Exception("Only restaurant owner can update this restaurant");
        }
        mapper.map(updatedRestaurant, restaurant);
        return restaurantRepository.save(restaurant);

    }

    @Override
    @Transactional
    public Boolean deleteRestaurant(User byUsername, Long Restaurantid) throws EntityNotFoundException {
        Restaurant restaurant = restaurantRepository.findById(Restaurantid).orElseThrow();
        long id = restaurant.getOwner().getId();
        long id1 = byUsername.getId();
        if(!Objects.equals(id, id1)){
            return false;
        }
        else{
            restaurantRepository.delete(restaurant);
            return true;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurant> searchRestaurant(String searchedWord) {
        List<Restaurant> bySearchQuery = restaurantRepository.findBySearchQuery(searchedWord);
        return bySearchQuery;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Restaurant> findAllRestaurants(Pageable pageable) {
        return restaurantRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Restaurant> findOpenRestaurants() {
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        return restaurantList.stream()
                .filter(Restaurant::isOpen)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Restaurant findRestaurantById(Long Restaurantid) throws Exception {
        Optional<Restaurant> byId = restaurantRepository.findById(Restaurantid);
        if(byId.isEmpty()){
            throw new Exception("Restaurant Not Found");
        }
        return byId.get();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResturantDto> getRestaurantByUserId(Long userId) throws EntityNotFoundException {
        log.info("Fetching restaurants for user ID: {}", userId);

        // ✅ JPQL Query - Main results
        List<Restaurant> byOwnerId = restaurantRepository.findByOwnerId(userId);

        // ✅ Native SQL Query - Performance check
        List<Restaurant> byOwnerIdsql = restaurantRepository.findByOwnerIdsql(userId);
        log.info("Native SQL results count: {}", byOwnerIdsql.size());

        // ✅ Convert to DTO
        return byOwnerId.stream()
                .map(restaurant -> mapper.map(restaurant, ResturantDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResturantDto addToFavourite(Long restaurantId, User user) throws Exception {
        Restaurant restaurant = findRestaurantById(restaurantId);
        // Checking using ID
        boolean alreadyFavorite = user.getFavorite().stream()
                .anyMatch(fav -> fav.getId().equals(restaurantId));
        if (alreadyFavorite) {
            user.getFavorite().removeIf(fav -> fav.getId().equals(restaurantId));
        } else {
            user.getFavorite().add(restaurant);
        }
        userRepository.save(user);
        return mapper.map(restaurant, ResturantDto.class);
    }

    @Override
    @Transactional
    public Restaurant updateRestaurantStatus(Long restaurantId) throws Exception {
        Restaurant restaurant = findRestaurantById(restaurantId);
        restaurant.setOpen(!restaurant.isOpen());
        return restaurant;
    }
}
