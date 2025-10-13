package com.Food.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.Food.Model.Address;
import com.Food.Model.Order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
	private String username;	
	private String email;



	private List<Order> orders = new ArrayList<>();


	private Set<RestaurantDto> favorite = new HashSet<>();


	private List<Address> addresses = new ArrayList<>();
}
