package com.ClassSpace.FeedbackFeature.Respositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ClassSpace.FeedbackFeature.Entity.Feedback;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
	
}
