package com.Food.Response;

import com.Food.Model.USER_ROLE;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
	
	 
	private String message;
	private String token;
	private String username;
	private USER_ROLE role;
	private Long userId;
	
	
	

}
