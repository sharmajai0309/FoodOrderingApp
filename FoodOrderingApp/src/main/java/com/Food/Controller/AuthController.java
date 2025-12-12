package com.Food.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Food.JwtConfig.Jwtutil;
import com.Food.Model.Cart;
import com.Food.Model.User;
import com.Food.Repository.ICartRepository;
import com.Food.Repository.IUserRepository;
import com.Food.Response.AuthResponse;
import com.Food.Service.ServiceImpl.UserServiceImpl;
import com.Food.request.LoginRequest;
import com.Food.Response.LoginResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final IUserRepository userRepo;  // Changed to lowercase for Java naming conventions
    private final PasswordEncoder passwordEncoder;
    private final Jwtutil jwtutil;
    private final UserServiceImpl userService;
    private final ICartRepository cartrepo;
    private final AuthenticationManager authenticationManager;  // Added AuthenticationManager

    // Constructor Injection - Added AuthenticationManager
    public AuthController(IUserRepository userRepo, PasswordEncoder passwordEncoder, Jwtutil jwtutil, 
            UserServiceImpl userService, ICartRepository cartrepo, AuthenticationManager authenticationManager) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtutil = jwtutil;
        this.userService = userService;
        this.cartrepo = cartrepo;
        this.authenticationManager = authenticationManager;  // Initialize AuthenticationManager
    }
     
    // signUp User
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) {
        log.info("Request to create user with username: {}", user.getUsername());
        
        // Check if user already exists by email
        User existEmail = userRepo.findByEmail(user.getEmail());
        if (existEmail != null) {
            log.error("User registration failed: Email already exists - {}", user.getEmail());
            throw new RuntimeException("User already exists with this email");
        }
        
        // Check if user already exists by username
        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            log.error("User registration failed: Username already exists - {}", user.getUsername());
            throw new RuntimeException("User already exists with this username");
        }
        
        log.info("No existing user found with email: {}", user.getEmail());
        log.info("Creating new user with username: {}", user.getUsername());
        
        // Create new User 
        User newUser = User.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .password(passwordEncoder.encode(user.getPassword()))  // Encode password
                .build();
        User savedUser = userRepo.save(newUser);
        log.info("User created successfully with username: {}", savedUser.getUsername());
        
        // Create cart for new user
        Cart cart = Cart.builder()
                .customer(savedUser)
                .build();
        cartrepo.save(cart);
        log.info("Cart created successfully for user: {}", savedUser.getUsername());
     

        String generatedToken = jwtutil.createToken(savedUser.getUsername());
        log.info("JWT token generated for user: {}", savedUser.getUsername());
        
        log.info("Returning AuthResponse for user: {}", savedUser.getUsername());
        
        // Return auth response with token
        return ResponseEntity.ok(AuthResponse.builder()
                .message("User Registered successfully")
                .token(generatedToken)
                .username(savedUser.getUsername())
                .role(savedUser.getRole())
                .userId(savedUser.getId())
                .build());
    }
    
    
    
    
    // Login User 
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUserHandler(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        
        log.info("Login attempt for username: {}", username);
        
        try {
            
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            
            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Generate token
            String generatedToken = jwtutil.createToken(authentication.getName());
            
            // Get user details for response
            User user = userService.findByUsername(username);
            
            log.info("User logged in successfully: {}", username);
            
            return ResponseEntity.ok(LoginResponse.builder()
                    .message("User logged in successfully")
                    .token(generatedToken)
                    .username(user.getUsername())
                    .role(user.getRole())
                    .build());
                    
        } catch (BadCredentialsException e) {
            log.error("Invalid login attempt for username: {}", username);
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}