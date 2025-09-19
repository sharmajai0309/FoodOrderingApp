package com.Food.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class webConfig {
	
	//Password Encoder Bean
	
	@Bean
	PasswordEncoder passwordEncoder() { // passwncodEncoder -> passwordEncoder
	    return new BCryptPasswordEncoder();
	}
	@Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
	
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		// Configure to skip null values during mapping
		modelMapper.getConfiguration()
				.setSkipNullEnabled(true);
		return modelMapper;
	}
	
	

}
