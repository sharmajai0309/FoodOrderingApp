package com.Food.Model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Setter
@Getter
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
	@Enumerated(EnumType.STRING)
	private USER_ROLE role;
	
	
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "customer")
	@JsonIgnore
	@Builder.Default
	private List<Order> orders = new ArrayList<>();

	@ManyToMany
	@JoinTable(name = "user_favorite")
	@Builder.Default
	@JsonIgnore
	private Set<Restaurant> favorite = new HashSet<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL,orphanRemoval = true)
	@Builder.Default
	@JsonIgnore
	private List<Address> addresses = new ArrayList<>();





}
