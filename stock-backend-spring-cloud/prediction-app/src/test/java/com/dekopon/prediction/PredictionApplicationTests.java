package com.dekopon.prediction;

import com.dekopon.prediction.controller.PredictionDataController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PredictionApplicationTests {

	@Autowired
	PredictionDataController predictionDataController;

	@Test
	void contextLoads() {
		predictionDataController.getModelScore("AAPL", "2023-07-08T00:00:00Z", "2023-07-10T00:00:00Z");
	}

}
