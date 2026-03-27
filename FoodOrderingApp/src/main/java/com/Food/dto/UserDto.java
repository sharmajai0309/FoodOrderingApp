package com.Food.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.Food.Model.Address;
import com.Food.Model.Order;
import com.Food.Model.USER_ROLE;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
	private Long id;
	private String username;	
	private String email;
	private USER_ROLE role;

	@Builder.Default
	private List<Order> orders = new ArrayList<>();

	@Builder.Default
	private Set<RestaurantDto> favorite = new HashSet<>();

	@Builder.Default
	private List<Address> addresses = new ArrayList<>();
}
