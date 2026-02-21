package com.classspace_backend.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "feedback",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"lecture_id", "hashed_student_key"})
    }
)
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long feedbackId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    // 🔐 ANONYMOUS IDENTIFIER (NO student_id)
    @Column(name = "hashed_student_key", nullable = false, length = 64)
    private String hashedStudentKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Understand understand;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "star_rating")
    private Integer starRating;

    @Column(name = "is_valid")
    private Boolean isValid = true;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
    }

    public enum Understand {
        YES,
        NO
    }

    // ===== getters & setters =====

    public Long getFeedbackId() {
        return feedbackId;
    }

    public Lecture getLecture() {
        return lecture;
    }

    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }

    public String getHashedStudentKey() {
        return hashedStudentKey;
    }

    public void setHashedStudentKey(String hashedStudentKey) {
        this.hashedStudentKey = hashedStudentKey;
    }

    public Understand getUnderstand() {
        return understand;
    }

    public void setUnderstand(Understand understand) {
        this.understand = understand;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getStarRating() {
        return starRating;
    }

    public void setStarRating(Integer starRating) {
        this.starRating = starRating;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean valid) {
        isValid = valid;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
}
