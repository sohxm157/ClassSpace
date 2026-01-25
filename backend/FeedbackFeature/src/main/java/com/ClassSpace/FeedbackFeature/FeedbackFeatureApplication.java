package com.ClassSpace.FeedbackFeature;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"com.ClassSpace.FeedbackFeature.Controllers",
		"com.ClassSpace.FeedbackFeature.Services",
		"com.ClassSpace.FeedbackFeature.Repository",
		"com.ClassSpace.FeedbackFeature.Entity",
		"com.ClassSpace.FeedbackFeature.DTO"
})
public class FeedbackFeatureApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeedbackFeatureApplication.class, args);
	}

}
