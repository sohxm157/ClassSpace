package com.ClassSpace.FeedbackFeature.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Entity
@Table(name = "feedback",uniqueConstraints = {
		@UniqueConstraint(columnNames = {"lecture_id","student_id"})
})
@Data
public class Feedback {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="feedback_id")
	private int feedbackId;
	
	@Column(name="lecture_id",nullable=false)
	private int lectureId;
	
	@Column(name="student_id",nullable=false)
	private String studentId;
	
	@Enumerated(EnumType.STRING)
	@Column(columnDefinition="ENUM('YES','NO')")
	private Understand understand;
	
	@Column(columnDefinition="TEXT")
	private String comment;
	
	@Min(1)
	@Max(5)
	@Column(name="star_rating",columnDefinition="INT CHECK (star_rating BETWEEN 1 and 5)")
	private int starRating;
	
	@Column(name = "is_valid")
	private boolean isValid = true;
	
	@Column(name="submitted_at",nullable=false,insertable=false,columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime submittedAt;
	
	public enum Understand{
		YES,NO
	}
}
