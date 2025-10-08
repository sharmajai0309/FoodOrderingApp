package com.Food.dto;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// This class is a Data Transfer Object (DTO) for Restaurant information.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDto {

	   @NotNull
	   private Long id;

	    @NotBlank
	    @NotNull
	    @Length(min = 1, max = 10, message = "Title must be Short")
	    private String title;


	    private List<String> images;
 
	    @Length(min = 1, max = 100,message = "Description must be between 1 and 100 characters")
	    private String description;
	
	
	
}
