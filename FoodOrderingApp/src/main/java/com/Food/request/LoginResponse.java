package com.Food.request;

import com.Food.Model.USER_ROLE;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginResponse {
	
	private String message;
    private String token;
    private String username;
    private USER_ROLE role;
    
    

}
