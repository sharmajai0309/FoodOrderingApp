package com.Food.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Restaurant {
	

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Long id;
	
	@ManyToOne
	private User owner;
	private String name;
	private String description;
	private String cusineType;
	
	
	@OneToOne
	private Address address;
	
	@Embedded
	private ContactInformation contactInformation;
	
	
	private String openingHours;
	
	
	@OneToMany(mappedBy = "restaurant" ,cascade = CascadeType.ALL,orphanRemoval = true)
	private List<Order> orders = new ArrayList<>();
	
	
	@ElementCollection
	@Column(length =  1000)
	private List<String> images;
	
	private LocalDateTime resgistrationDate;
	
	private Boolean open;
	
	
	@OneToMany(mappedBy = "restaurant",cascade = CascadeType.ALL,orphanRemoval = true)
	private List<Food> foods  = new ArrayList<>();
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
