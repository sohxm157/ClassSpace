package com.classspace_backend.demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;



public class FeedbackDTO {
	
	private Long lectureId;
	
	private int studentId;
	
	private String understand;
	
	private String comment;
	
	@Min(1)
	@Max(5)
	private int starRating;

	public FeedbackDTO() {}

	public FeedbackDTO( @Min(1) @Max(5) int starRating,String understand, String comment) {
		super();
		
		this.understand = understand;
		this.comment = comment;
		this.starRating = starRating;
	}

	public Long getLectureId() {
		return lectureId;
	}

	public void setLectureId(Long lectureId) {
		this.lectureId = lectureId;
	}

	public int getStudentId() {
		return studentId;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

	public String getUnderstand() {
		return understand;
	}

	public void setUnderstand(String understand) {
		this.understand = understand;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getStarRating() {
		return starRating;
	}

	public void setStarRating(int starRating) {
		this.starRating = starRating;
	}
	
	
}

