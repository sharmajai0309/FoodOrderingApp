package com.Food.dto;

import java.util.ArrayList;
import java.util.List;

import com.Food.Model.Address;
import com.Food.Model.Order;

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

	private List<RestaurantDto>favorite = new ArrayList<>();

	private List<Address> addresses = new ArrayList<>();
}
