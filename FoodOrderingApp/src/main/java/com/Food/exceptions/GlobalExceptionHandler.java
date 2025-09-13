package com.Food.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ JWT Token Expired
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiError> handleExpiredJwtException(ExpiredJwtException ex) {
        ApiError apiError = new ApiError();
        apiError.setError("JWT_TOKEN_EXPIRED");
        apiError.setMessage("Token has expired. Please login again.");
        apiError.setStatusCode(HttpStatus.UNAUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    // ✅ Invalid JWT Token
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ApiError> handleSignatureException(SignatureException ex) {
        ApiError apiError = new ApiError();
        apiError.setError("INVALID_JWT_TOKEN");
        apiError.setMessage("Invalid token signature.");
        apiError.setStatusCode(HttpStatus.UNAUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    // ✅ Malformed JWT Token
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ApiError> handleMalformedJwtException(MalformedJwtException ex) {
        ApiError apiError = new ApiError();
        apiError.setError("MALFORMED_JWT_TOKEN");
        apiError.setMessage("Invalid token format.");
        apiError.setStatusCode(HttpStatus.UNAUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    // ✅ Authentication Failed
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex) {
        ApiError apiError = new ApiError();
        apiError.setError("AUTHENTICATION_FAILED");
        apiError.setMessage("Authentication failed. Invalid credentials.");
        apiError.setStatusCode(HttpStatus.UNAUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    // ✅ Access Denied (Role based)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex) {
        ApiError apiError = new ApiError();
        apiError.setError("ACCESS_DENIED");
        apiError.setMessage("You don't have permission to access this resource.");
        apiError.setStatusCode(HttpStatus.FORBIDDEN);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
    }

    // ✅ User Not Found
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ApiError apiError = new ApiError();
        apiError.setError("USER_NOT_FOUND");
        apiError.setMessage("User not found with given credentials.");
        apiError.setStatusCode(HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    // ✅ Generic Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        ApiError apiError = new ApiError();
        apiError.setError("INTERNAL_SERVER_ERROR");
        apiError.setMessage("Something went wrong. Please try again later.");
        apiError.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }
    

    
   

}