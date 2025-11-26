package com.Food;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EntityScan(basePackages = {"com.Food.Model"})
@EnableCaching
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class FoodOrderingAppApplication {

	/**
	 * Main method to run the Food Ordering Application.
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args) {

		SpringApplication.run(FoodOrderingAppApplication.class, args);

	}


	

}
