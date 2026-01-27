package com.ClassSpace.FeedbackFeature.Controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ClassSpace.FeedbackFeature.DTO.FeedbackDTO;
import com.ClassSpace.FeedbackFeature.Services.FeedbackService;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {
	private final FeedbackService feedbackService;
	
	public FeedbackController(FeedbackService feedbackService) {
		this.feedbackService = feedbackService;
	}
	
	@PostMapping("/post")
	public ResponseEntity<?> postFeedback(@RequestBody FeedbackDTO feedbackDTO){
		try {
		feedbackService.save(feedbackDTO);
		return ResponseEntity.status(HttpStatus.OK).body(feedbackDTO);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This student has already given feedback for this lecture.");
		}
	}
	
	@GetMapping("/showAll")
	public ResponseEntity<?> showAllFeedbacks(){
		try {
			List<String> feedbacks = feedbackService.getAllFeedbacks();
			return ResponseEntity.status(HttpStatus.OK).body(feedbacks);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No feedbacks found.");
		}
	}
	
	//Only the admin is allowed to delete all feedbacks here
	//in the security filter chain, we will check if the role is admin only then this endpoint will be accessible
	@DeleteMapping("/deleteAll")
	public ResponseEntity<?> deleteAllFeedbacks(){
		feedbackService.deleteAllFeedbacks();
		return ResponseEntity.status(HttpStatus.OK).body("All The Feedbacks Deleted Successfully");
	}
	
}
