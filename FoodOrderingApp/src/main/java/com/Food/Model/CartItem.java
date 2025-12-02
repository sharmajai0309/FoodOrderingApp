package com.Food.Model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CartItem {

	
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Long id;
	
	
	@ManyToOne
	@JsonIgnore
	private Cart cart;
	
	
	@ManyToOne
	private Food food;
	
	private int quantity;

	private long totalPrize;

	@ElementCollection
	private List<String>ingredients;
	
	
	
}
