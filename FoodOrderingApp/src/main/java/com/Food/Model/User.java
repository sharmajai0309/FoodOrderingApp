package com.Food.Model;

import java.util.ArrayList;  
import java.util.List;



import com.Food.dto.ResturantDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "username", nullable = false)
	private String username;
	
	@Column(name = "email", unique = true, nullable = false)
	private String email;
	
	@Column(name = "password", nullable = false)
	private String password;
	
	//Enum for role
	private USER_ROLE role;
	
	
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "customer")
	private List<Order> orders = new ArrayList<>();
	
	@ElementCollection
	private List<ResturantDto>favorite = new ArrayList<>();
	
	@OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
	private List<Address> addresses = new ArrayList<>();
	
	
	

}
