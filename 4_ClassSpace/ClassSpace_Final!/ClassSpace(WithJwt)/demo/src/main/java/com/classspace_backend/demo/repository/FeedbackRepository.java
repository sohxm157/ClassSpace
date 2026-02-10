package com.classspace_backend.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.classspace_backend.demo.entity.Feedback;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // 🔐 Prevent duplicate feedback (one per student per lecture)
    boolean existsByLecture_LectureIdAndHashedStudentKey(
            Long lectureId,
            String hashedStudentKey
    );

    // 👨‍🏫 Teacher-only: anonymous text feedback for ONE lecture
    @Query("""
        SELECT f.comment
        FROM Feedback f
        WHERE f.lecture.lectureId = :lectureId
          AND f.isValid = true
          AND f.comment IS NOT NULL
    """)
    List<String> findCommentsByLecture(Long lectureId);

    // 📊 Aggregation (stars + understood ratio)
    List<Feedback> findByLecture_LectureId(Long lectureId);
}
