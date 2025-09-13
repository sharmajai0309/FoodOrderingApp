package com.Food.Model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CartItem {

	
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Long id;
	
	
	@ManyToOne
	private Cart cart;
	
	
	@ManyToOne
	private Food food;
	
	private int quantity;
	
	
	
	private long totalPrize;
	
	private List<String>ingredients;
	
	
	
}
