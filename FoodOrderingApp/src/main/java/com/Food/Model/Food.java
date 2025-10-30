package com.Food.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;


@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	private List<IngredientItem> ingredients = new ArrayList<>();


	private LocalDateTime createdDate;
	
	
	
	
	
	
	
	
	
	
	
}
