package com.Food.JwtConfig;

import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

@Component
public class Jwtutil {
    
    @Value("${app.secret.jai}")
    private String secretkey;
    
    private static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 1000; // 5 hours in milliseconds
    
    // Create SecretKey once
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretkey.getBytes());
    }
    
    // FIXED: Simple token creation
    public String createToken(String username) {
        return Jwts.builder()
                  .setSubject(username)
                  .setIssuedAt(new Date())
                  .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                  .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                  .compact();
    }

    // Extracting username from the token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    // FIXED: Simple and correct parsing
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretkey.getBytes())  // Use raw bytes
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) { 
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    // DEBUG: Check what's happening with your token
    public void debugToken(String token) {
        System.out.println("=== TOKEN DEBUG ===");
        System.out.println("Raw token: " + token);
        System.out.println("Token length: " + token.length());
        System.out.println("Number of dots: " + countDots(token));
        
        String[] parts = token.split("\\.");
        System.out.println("Parts count: " + parts.length);
        for (int i = 0; i < parts.length; i++) {
            System.out.println("Part " + i + " length: " + parts[i].length());
        }
    }
    
    private int countDots(String token) {
        int count = 0;
        for (char c : token.toCharArray()) {
            if (c == '.') count++;
        }
        return count;
    }
}