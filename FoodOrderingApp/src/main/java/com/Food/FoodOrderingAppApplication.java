package com.Food;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@Slf4j
@EntityScan(basePackages = {"com.Food.Model"})
@EnableCaching
public class FoodOrderingAppApplication {

	/**
	 * Main method to run the Food Ordering Application.
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		log.info("Food Ordering Application is starting...");
		SpringApplication.run(FoodOrderingAppApplication.class, args);
		log.info("Food Ordering Application is Started Successfully.");
	}
	

}
