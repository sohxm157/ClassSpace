package com.ClassSpace.FeedbackFeature.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackDTO {
	
	private int lectureId;
	
	private int studentId;
	
	private String understand;
	
	private String comment;
	
	@Min(1)
	@Max(5)
	private int starRating;
	
	
}
