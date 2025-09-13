package com.Food.request;

import com.Food.Model.USER_ROLE;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

	@NotBlank(message = "Username is mandatory")
	private String username;
	
	@Email(message = "Email should be valid")
	private String email;
	
	@NotBlank(message = "Password is mandatory")
	private String password;
	

}
