package com.Food.JwtConfig;

import java.security.SecureRandom;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Component
public class Jwtutil {
	
	@Value("${app.secret.jai}")
	private String secretkey;
	
//	@Autowired
//	SecureRandom secureRandom ;
	
	private static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60; // 5 hours
	
	
	
	//creating token for user
	public String createToken(String username) {
		return Jwts.builder()				
				  .subject(username) 
		            .issuedAt(new Date())
		            .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
		            .signWith(SignatureAlgorithm.HS256, secretkey.getBytes())
		            .compact();
	}

   	// Extracting username from the token
	public String extractUsername(String token) {

		return extractClaim(token, claims -> claims.getSubject());
	}
	
	// Extracting email from the claims
	public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }
	// Extracting role from the token
//	public String extractAuthority(String token) {
//		return extractClaim(token, claims -> claims.get("role", String.class));
//	}
	
	// Extracting all claims from the token
	private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretkey.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) { 
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
	
	public boolean validateToken(String token, UserDetails userDetails) {
	    String username = extractUsername(token);
	    boolean isExpired = extractAllClaims(token).getExpiration().before(new Date());
	    return username.equals(userDetails.getUsername()) && !isExpired;
	}
	
	
	
	
	
	
	

}
