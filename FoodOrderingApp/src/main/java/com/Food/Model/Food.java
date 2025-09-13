package com.Food.Model;

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
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
	
	
	
	private Boolean isAvailable;
	
	@ManyToOne
	private Restaurant restaurant;
	
	private Boolean isVegitarian;
	private Boolean isSeasonal;
	
	
	@ManyToMany
	private List<IngredientItem> ingredients = new ArrayList<>();
	
	private Date createdDate;
	
	
	
	
	
	
	
	
	
	
	
}
