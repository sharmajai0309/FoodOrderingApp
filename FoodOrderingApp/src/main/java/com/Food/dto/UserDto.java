package com.Food.dto;

import java.util.ArrayList;
import java.util.List;

import com.Food.Model.Address;
import com.Food.Model.Order;
import com.Food.Model.USER_ROLE;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
	private String username;	
	private String email;
	private List<Order> orders = new ArrayList<>();
	private List<ResturantDto>favorite = new ArrayList<>();
	private List<Address> addresses = new ArrayList<>();
}
