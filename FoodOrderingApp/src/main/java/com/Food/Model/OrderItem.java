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
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
	
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Long id;
	
	@ManyToOne
	private Food food;
	
	private int quantity;
	
	private Long totalprice;
	
	private List<String> ingredients;

}
