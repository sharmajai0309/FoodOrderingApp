package com.Food.Service;


import java.util.Collection;
import java.util.List;

import javax.naming.OperationNotSupportedException;

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
	public Restaurant updateRestaurant(@NotNull Long Restaurantid ,@Valid CreateRestaurantRequest updatedRestaurant) throws Exception;
	
	
    //Delete Restaurant
	public void deleteRestaurant(Long Restaurantid) throws EntityNotFoundException;

	//Search Restaurant
	public List<Restaurant> searchRestaurant(String searchedWord);
	
	
	//find all Restaurant
	Page<Restaurant> findAllRestaurants(Pageable pageable);
	
	// find all open Restaurant
	Collection<Restaurant> findOpenRestaurants();
	
	//find Restaurant By ID
	public Restaurant findRestaurantById(Long Restaurantid) throws Exception ;

	public List<Restaurant> getRestaurantByUserId(Long userId) throws EntityNotFoundException;
	
	
	//Adding Restaurant to a favourite list
	public ResturantDto addToFavourite(Long Restaurantid,User user) throws Exception;
	
	
	//For Updating Restaurant Status
	public Restaurant updateRestaurantStatus(Long id) throws Exception;
	
	
	
	
}
