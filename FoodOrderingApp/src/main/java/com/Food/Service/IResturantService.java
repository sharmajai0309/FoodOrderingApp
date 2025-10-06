package com.Food.Service;


import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.Food.Model.Restaurant;
import com.Food.Model.User;
import com.Food.dto.ResturantDto;
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
	
	// find all open Restaurant
	Collection<Restaurant> findOpenRestaurants();
	
	//find Restaurant By ID(dual params
	public Restaurant findRestaurantById(Long restaurantId) throws Exception ;

	//find Restaurant By ID(single param )
	public Restaurant findRestaurantById(Long restaurantId,User currentUser) throws Exception ;

	public List<ResturantDto> getRestaurantByUserId(Long userId) throws EntityNotFoundException;
	
	
	//Adding Restaurant to a favourite list
	public ResturantDto addToFavourite(Long restaurantId,User user) throws Exception;
	
	
	//For Updating Restaurant Status
	public Restaurant updateRestaurantStatus(Long restaurantId,User currentUser) throws Exception;
	
	
	
	
}
