package com.ClassSpace.FeedbackFeature.Services;

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
		Feedback feedback = new Feedback();
		BeanUtils.copyProperties(dto, feedback);
		feedbackRepository.save(feedback);
	}
}
