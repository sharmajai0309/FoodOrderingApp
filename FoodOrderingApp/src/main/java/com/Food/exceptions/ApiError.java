package com.Food.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
	
	
	 private LocalDateTime timestamp;
	    private String error;
	    private String message;
	    private HttpStatus statusCode;

}
