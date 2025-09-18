package com.Food.Model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
	
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Long id;

	@JsonIgnore
	@ManyToOne
	private Order order;
	
	@ManyToOne
	private Food food;
	
	private int quantity;
	
	private Long totalprice;

	@ElementCollection
	private List<String> ingredients;

}
