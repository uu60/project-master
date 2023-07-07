package com.dekopon.prediction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PredictionApplication {

	public static void main(String[] args) {
		SpringApplication.run(PredictionApplication.class, args);
	}

}
