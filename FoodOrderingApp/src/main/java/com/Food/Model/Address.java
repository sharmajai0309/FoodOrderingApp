package com.Food.Model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Address {
	
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Long id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;
	private String street;
	private String city;
	private String zipCode;
	private String country;



}
