package com.Food.Service.ServiceImpl;
import com.Food.Model.Restaurant;
import com.Food.Model.User;
import com.Food.Repository.IAddressRepository;
import com.Food.Repository.IRestaurantRepository;
import com.Food.Service.IResturantService;
import com.Food.Service.IUserServices;
import com.Food.dto.ResturantDto;
import com.Food.request.CreateRestaurantRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements IResturantService {

    //Constructor Injection
    private final IUserServices userService;
    private final IAddressRepository addressRepository;
    private final IRestaurantRepository restaurantRepository;

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
    public Restaurant updateRestaurant(Long Restaurantid, CreateRestaurantRequest updatedRestaurant) throws Exception {
        return null;
    }

    @Override
    public void deleteRestaurant(Long Restaurantid) throws EntityNotFoundException {

    }

    @Override
    public List<Restaurant> searchRestaurant() {
        return List.of();
    }

    @Override
    public Page<Restaurant> findAllRestaurants(Pageable pageable) {
        return null;
    }

    @Override
    public Collection<Restaurant> findOpenRestaurants() {
        return List.of();
    }

    @Override
    public Restaurant findRestaurantById(Long Restaurantid) throws Exception {
        return null;
    }

    @Override
    public Restaurant getRestaurantByUserId(Long userId) throws EntityNotFoundException {
        return null;
    }

    @Override
    public ResturantDto addToFavourite(Long Restaurantid, User user) throws Exception {
        return null;
    }

    @Override
    public Restaurant updateRestaurantStatus(Long id) throws Exception {
        return null;
    }
}
