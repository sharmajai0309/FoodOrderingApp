package com.Food.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;
	
	@ManyToOne
	@JsonIgnore
	private Restaurant restaurant;

	@JsonIgnore
	@OneToMany(mappedBy = "foodcategory",cascade = CascadeType.ALL,orphanRemoval = true)
	private List<Food> foods = new ArrayList<>();
	
	
	
	
	

}
