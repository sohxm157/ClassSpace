package com.ClassSpace.FeedbackFeature.Respositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ClassSpace.FeedbackFeature.Entity.Feedback;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
	public int findByLectureId(int lectureId);
	
	public int findByStudentId(int studentId);
	
	public boolean existsByLectureIdAndStudentId(int lectureId, int studentId);
	
	@Query("SELECT f.comment FROM Feedback f WHERE f.isValid = true")
	public List<String> findAllComments();
}
