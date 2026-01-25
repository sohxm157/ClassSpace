package com.ClassSpace.FeedbackFeature.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ClassSpace.FeedbackFeature.DTO.FeedbackDTO;

@RestController("/feedback")
public class FeedbackController {
	
	
	@PostMapping("/post")
	public ResponseEntity<?> postFeedback(@RequestBody FeedbackDTO feedbackDTO){
		
		return ResponseEntity.status(HttpStatus.OK).body(feedbackDTO);
	}
}
