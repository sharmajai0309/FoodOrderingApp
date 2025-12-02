 package com.Food.Model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.*;


 @Entity
 @Setter
 @Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cart {
	
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Long id;


	@OneToOne
	private User customer;
	
	private Long total;
	
	
	@OneToMany(mappedBy = "cart",cascade = CascadeType.ALL,orphanRemoval = true)
	@Builder.Default
	private List<CartItem> items =  new ArrayList<>();
	
	

}
