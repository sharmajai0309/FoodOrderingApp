package com.Food.Model;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ContactInformation {


	
	private String email;
	private String mobile;
	private String twitter;
	private String instagram;
	private String facebook;
	
	
	
	
	
	

}
