package com.deliveryService.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DeliveryPartnerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeliveryPartnerApplication.class, args);
	}

}
