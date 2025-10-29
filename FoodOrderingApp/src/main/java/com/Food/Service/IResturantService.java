package com.Food.Service;


import java.util.Collection;
import java.util.List;

import com.Food.Model.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.Food.Model.Restaurant;
import com.Food.Model.User;
import com.Food.dto.RestaurantDto;
import com.Food.request.CreateRestaurantRequest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface IResturantService {
	
	
	//For CreatingRestaurant owner 
	public Restaurant createRestaurant(CreateRestaurantRequest req,User user);

	//For Restaurant owner 
	public Restaurant updateRestaurant(@NotNull Long restaurantId ,@Valid CreateRestaurantRequest updatedRestaurant) throws Exception;
	
	
    //Delete Restaurant
	public Boolean deleteRestaurant(User byUsername, Long restaurantId) throws EntityNotFoundException;

	//Search Restaurant
	public List<Restaurant> searchRestaurant(String searchedWord);
	
	
	//find all Restaurant
	Page<Restaurant> findAllRestaurants(Pageable pageable);

	public List<Restaurant> findAllRestaurants();

	// find all open Restaurant
	Collection<Restaurant> findOpenRestaurants();
	
	//find Restaurant By ID(dual params
	public Restaurant findRestaurantById(Long restaurantId) throws Exception ;

	//find Restaurant By ID(single param )
	public Restaurant findRestaurantById(Long restaurantId,User currentUser) throws Exception ;

	public List<RestaurantDto> getRestaurantByUserId(Long userId) throws EntityNotFoundException;
	
	
	//Adding Restaurant to a favourite list
	public RestaurantDto addToFavourite(Long restaurantId, User user) throws Exception;
	
	
	//For Updating Restaurant Status
	public Restaurant updateRestaurantStatus(Long restaurantId,User currentUser) throws Exception;




	
	
	
}
