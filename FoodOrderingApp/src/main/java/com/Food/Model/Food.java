package com.Food.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.*;


@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Food {

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Long id;
	
	private String name;
	
	
	private String description;
	private Long price;
	
	@ManyToOne
	private Category foodcategory;
	
	@Column(length =  1000)
	@ElementCollection
	private List<String> images;
	
	
	
	private Boolean isAvailable = true;
	
	@ManyToOne
	private Restaurant restaurant;
	
	private Boolean isVegetarian;
	private Boolean isSeasonal;
	
	
	@ManyToMany
	private List<IngredientItem> ingredients = new ArrayList<>();
	
	private LocalDateTime createdDate;
	
	
	
	
	
	
	
	
	
	
	
}
