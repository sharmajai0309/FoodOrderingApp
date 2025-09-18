package com.Food.request;

import java.util.List;

import com.Food.Model.Address;
import com.Food.Model.ContactInformation;

import lombok.Data;

@Data
public class CreateRestaurantRequest {

	
	private Long id;	
	private String name;
	private String description;
	private String cusineType;
	private Address address;
	private ContactInformation contactInformation;
	private String openingHours;
	private List<String> images;
	
	
	
}
