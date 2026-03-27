package com.deliveryService.service.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.deliveryService.service.api_output.ApiResponse;
import com.deliveryService.service.exception.customexception.baseexception.DeliveryPartnerException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // // **1. JWT Token Expired** - Standard way
    // @ExceptionHandler(ExpiredJwtException.class)
    // public ResponseEntity<Map<String, Object>>
    // handleExpiredJwtException(ExpiredJwtException ex) {
    // log.warn("JWT token expired: {}", ex.getMessage());
    //
    // Map<String, Object> errorResponse = new HashMap<>();
    // errorResponse.put("timestamp", LocalDateTime.now());
    // errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
    // errorResponse.put("error", "JWT_TOKEN_EXPIRED");
    // errorResponse.put("message", "Token has expired. Please log in again.");
    // errorResponse.put("path", "N/A");
    //
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    // }
    //
    // // **2. Invalid JWT Token**
    // @ExceptionHandler(SignatureException.class)
    // public ResponseEntity<Map<String, Object>>
    // handleSignatureException(SignatureException ex) {
    // log.warn("Invalid JWT signature: {}", ex.getMessage());
    //
    // Map<String, Object> errorResponse = new HashMap<>();
    // errorResponse.put("timestamp", LocalDateTime.now());
    // errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
    // errorResponse.put("error", "INVALID_JWT_TOKEN");
    // errorResponse.put("message", "Invalid token signature.");
    //
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    // }
    //
    // // **3. Malformed JWT Token**
    // @ExceptionHandler(MalformedJwtException.class)
    // public ResponseEntity<Map<String, Object>>
    // handleMalformedJwtException(MalformedJwtException ex) {
    // log.warn("Malformed JWT: {}", ex.getMessage());
    //
    // Map<String, Object> errorResponse = new HashMap<>();
    // errorResponse.put("timestamp", LocalDateTime.now());
    // errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
    // errorResponse.put("error", "MALFORMED_JWT_TOKEN");
    // errorResponse.put("message", "Invalid token format.");
    //
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    // }

    // **4. User Not Found**
    // @ExceptionHandler(UsernameNotFoundException.class)
    // public ResponseEntity<Map<String, Object>>
    // handleUsernameNotFoundException(UsernameNotFoundException ex) {
    // log.warn("User not found: {}", ex.getMessage());
    //
    // Map<String, Object> errorResponse = new HashMap<>();
    // errorResponse.put("timestamp", LocalDateTime.now());
    // errorResponse.put("status", HttpStatus.NOT_FOUND.value());
    // errorResponse.put("error", "USER_NOT_FOUND");
    // errorResponse.put("message", "User not found with given credentials.");
    //
    // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    // }

    // **5. Entity Not Found (404)**
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // **6. No Handler Found (404)**
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NoHandlerFoundException ex) {
        log.warn("API endpoint not found: {}", ex.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("API endpoint not found: " + ex.getRequestURL()));
    }

    // **7. Access Denied (403)**
    // @ExceptionHandler(AccessDeniedException.class)
    // public ResponseEntity<ApiResponse<Void>>
    // handleAccessDenied(AccessDeniedException ex) {
    // log.warn("Access denied: {}", ex.getMessage());
    // return ResponseEntity.status(HttpStatus.FORBIDDEN)
    // .body(ApiResponse.error("Access denied: " + ex.getMessage()));
    // }

    // **8. Authentication Failed (401)**
    // @ExceptionHandler(AuthenticationException.class)
    // public ResponseEntity<ApiResponse<Void>>
    // handleAuthentication(AuthenticationException ex) {
    // log.warn("Authentication failed: {}", ex.getMessage());
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    // .body(ApiResponse.error("Authentication failed: " + ex.getMessage()));
    // }

    // **9. Validation Errors (400) - MethodArgumentNotValidException**
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValid(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("Validation failed")
                .data(errors)
                .timestamp(java.time.Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // **10. Constraint Violation Errors (400)**
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(ConstraintViolationException ex) {
        log.warn("Constraint violation error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("Validation failed")
                .data(errors)
                .timestamp(java.time.Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // **10. Illegal Argument (400)**
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid input: " + ex.getMessage()));
    }

    // **11. Generic Exception (500) - Log with stack trace**
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllExceptions(Exception ex) {
        log.error("Internal server error: ", ex);

        // Cannot show Detailed errors logs in productions
        String errorMessage = "An internal server error occurred";

        // show Detailed errors logs in Development
        if (isDevelopmentEnvironment()) {
            errorMessage = "Internal server error: " + ex.getMessage();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(errorMessage));
    }

    @ExceptionHandler(DeliveryPartnerException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handlePartnerException(DeliveryPartnerException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("errorCode", ex.getErrorCode());
        error.put("message", ex.getMessage());

        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .success(false)
                .message("Partner Service Error")
                .data(error)
                .build();

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    private boolean isDevelopmentEnvironment() {
        // Check if running in development environment
        String profile = System.getProperty("spring.profiles.active", "development");
        return "development".equals(profile) || "dev".equals(profile);
    }
}