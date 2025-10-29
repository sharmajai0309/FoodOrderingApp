package com.Food.Service.ServiceImpl;

import com.Food.Model.Food;
import com.Food.Model.Restaurant;
import com.Food.Model.User;
import com.Food.Repository.IAddressRepository;
import com.Food.Repository.IRestaurantRepository;
import com.Food.Repository.IUserRepository;
import com.Food.Service.IResturantService;
import com.Food.Service.IUserServices;
import com.Food.dto.RestaurantDto;
import com.Food.exceptions.CustomException.RestaurantNotFoundException;
import com.Food.exceptions.CustomException.UnauthorizedAccessException;
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

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


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
           log.info("int update method");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info(username);
        Restaurant restaurant = findRestaurantById(Restaurantid);
        System.out.println(restaurant.getOwner());

        User currentUser = userService.findByUsername(username);
        if (!Objects.equals(restaurant.getOwner().getId(), currentUser.getId())) {
            throw new AccessDeniedException("Only restaurant owner can update this restaurant");
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
    public List<Restaurant> findAllRestaurants() {
        return restaurantRepository.findAll();
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
    public List<RestaurantDto> getRestaurantByUserId(Long userId) throws EntityNotFoundException {
        log.info("Fetching restaurants for user ID: {}", userId);

        // ✅ JPQL Query - Main results
        List<Restaurant> byOwnerId = restaurantRepository.findByOwnerId(userId);

        // ✅ Native SQL Query - Performance check
        List<Restaurant> byOwnerIdsql = restaurantRepository.findByOwnerIdsql(userId);
        log.info("Native SQL results count: {}", byOwnerIdsql.size());

        // ✅ Convert to DTO
        return byOwnerId.stream()
                .map(restaurant -> mapper.map(restaurant, RestaurantDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RestaurantDto addToFavourite(Long restaurantId, User user) throws Exception {
        log.info("Adding restaurant {} to favorites for user");
        Restaurant restaurant = findRestaurantById(restaurantId);
        // Checking using ID //state check
//        boolean wasRemoved = user.getFavorite().removeIf(fav -> fav.getId().equals(restaurantId));
//        if (!wasRemoved) {
//            user.getFavorite().add(restaurant);
//            log.info("Restaurant {} added to favorites for user {}", restaurantId, user.getId());
//        }
//        userRepository.save(user);
//        log.info("Restaurant favorites saved in database");
//
//        return mapper.map(restaurant, RestaurantDto.class);

//        real time database chek
//        Optional<User> currentuser = userRepository.findById(user.getId());
//
//        boolean wasRemoved = currentuser.get().getFavorite().removeIf(fav -> fav.getId().equals(restaurantId));
//        if(!wasRemoved){
//            currentuser.get().getFavorite().add(restaurant);
//            log.info("Restaurant {} added to favorites", restaurantId);
//        } else {
//            log.info("Restaurant {} removed from favorites", restaurantId);
//        }
//        userRepository.save(currentuser.get());
//        return mapper.map(restaurant,RestaurantDto.class);


        userRepository.toggleFavorite(user.getId(), restaurantId);

        return mapper.map(restaurant, RestaurantDto.class);

    }


    //Update Restaurant Status
    @Override
    @Transactional
    public Restaurant updateRestaurantStatus(Long restaurantId,User currentUser) throws Exception {
        Restaurant restaurant = findRestaurantById(restaurantId);
//        Only Owner can update restaurant status

        long id = restaurant.getOwner().getId();
        long id1 = currentUser.getId();
        if(!Objects.equals(id, id1)){
            throw new Exception(
                    "User " + currentUser.getId() + " is not authorized to update restaurant " + restaurantId);
        }
        else restaurant.setOpen(!restaurant.isOpen());
        log.info("Restaurant {} status updated to {} by user {}",
                restaurantId, restaurant.isOpen(), currentUser.getId());
        // Automatic save by @Transactional
        return restaurant;
    }



    //    find id without token info
    @Override
    public Restaurant findRestaurantById(Long restaurantId) throws Exception {
        log.info("Finding restaurant {}", restaurantId);
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        log.info("Restaurant {} found", restaurant);
        return restaurant.get();
    }

    //    find with token info
    @Override
    @Transactional(readOnly = true)
    public Restaurant findRestaurantById(Long restaurantId, User currentUser) throws UnauthorizedAccessException {
        log.info("Finding restaurant {} for user {}", restaurantId, currentUser.getId());

        // Input validation
        if (restaurantId == null || restaurantId <= 0) {
            throw new IllegalArgumentException("Invalid restaurant ID");
        }

        //  Find a restaurant or throw exception
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant Not Found"));

        // Authorization check
        if (restaurant.getOwner().getId() != currentUser.getId()) {
            throw new UnauthorizedAccessException("You are not authorized to access this restaurant");
        }
        log.info("Restaurant {} found for user {}", restaurantId, currentUser.getId());
        return restaurant;
    }
}
