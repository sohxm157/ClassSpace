package com.ClassSpace.FeedbackFeature.Services;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.ClassSpace.FeedbackFeature.DTO.FeedbackDTO;
import com.ClassSpace.FeedbackFeature.Entity.Feedback;
import com.ClassSpace.FeedbackFeature.Respositories.FeedbackRepository;

@Service
public class FeedbackService {
	
	private final FeedbackRepository feedbackRepository;
	
	
	public FeedbackService(FeedbackRepository feedbackRepository) {
		this.feedbackRepository = feedbackRepository;
	}
	
	public void save(FeedbackDTO dto) {
		boolean alreadyExists = feedbackRepository.existsByLectureIdAndStudentId(dto.getLectureId(), dto.getStudentId());
		
		if(alreadyExists) {
			throw new IllegalArgumentException("Feedback already exists for this lecture and student.");
		}
		
		Feedback feedback = new Feedback();
		BeanUtils.copyProperties(dto, feedback);
		feedback.setUnderstand(Feedback.Understand.valueOf(dto.getUnderstand()));
		System.out.println(feedback.getUnderstand());
		feedbackRepository.save(feedback);
	}
	
	public List<String> getAllFeedbacks(){
		List<String> feedbacks = feedbackRepository.findAllComments();
		
		if(feedbacks.isEmpty()) {
			throw new IllegalArgumentException("No feedbacks found.");
		}
		
		return feedbacks;
	}
}
