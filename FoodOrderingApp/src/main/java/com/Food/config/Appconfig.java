package com.Food.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.Food.JwtConfig.JwtAuthFilter;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class Appconfig {
	
	@Autowired
	public JwtAuthFilter jwtAuthFilter;
	
	@Autowired
    private UserDetailsService userDetailsService;

	
	
	
	
	//Security Filter Chain for JWT Token Validation and Role Based Authorization

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
					//public Routes
					.requestMatchers("/v1/user/**").permitAll()
					.requestMatchers("/api/customer/restaurants/search/**",
							"/api/customer/restaurants/allRestaurants/**"
					).permitAll()


					//Admin Routes
					.requestMatchers("/api/admin/**").hasAnyRole("RESTAURANT_ADMIN", "ADMIN")
					//general routes
					.requestMatchers("/api/**").authenticated()
					//Other routes
					.anyRequest().permitAll()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .csrf(csrf -> csrf.disable())
            .userDetailsService(userDetailsService)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));
        return http.build();
    }
    
    
    
    
    //Global CorsConfiguration
    
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
    	
    	return new CorsConfigurationSource() {
			
			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
			
				CorsConfiguration cfg =  new CorsConfiguration();
				cfg.setAllowedOrigins(Arrays.asList(

						"http://localhost:3000"
						));
				
				// Allowed methods for FrontEnd Team
				cfg.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
				cfg.setAllowCredentials(true);
				cfg.setAllowedHeaders(Collections.singletonList("*"));
				cfg.setExposedHeaders(Arrays.asList("Authorization"));
				cfg.setMaxAge(3600L);
				
				return cfg;
				
				
			}
		};
    }
       
}
