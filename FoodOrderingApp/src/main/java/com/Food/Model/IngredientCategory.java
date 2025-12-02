package com.Food.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class IngredientCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	
	private String name;


	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
    @ToString.Exclude
    private Restaurant restaurant;
	
	
	@OneToMany(mappedBy = "category",cascade = CascadeType.ALL,orphanRemoval = true)
	@JsonIgnore
	@ToString.Exclude
	private List<IngredientItem> ingredientItems = new ArrayList<>();

	
}
