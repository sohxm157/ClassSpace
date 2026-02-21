package com.classspace_backend.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class LectureDetailsDto {
    private Long lectureId;
    private Long timetableId;
    private String subject;
    private String className;
    private LocalDate lectureDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime cancelledAt;
    private String lectureType; // FUTURE / PAST / ONGOING

    private long totalStudents;
    private long expectedStudents;
    private long likelyAbsentStudents;
    private int presentCount; // Actual attendance count

    private List<FeedbackSummaryDto> feedbacks;

    // Getters and Setters
    public Long getLectureId() {
        return lectureId;
    }

    public void setLectureId(Long lectureId) {
        this.lectureId = lectureId;
    }

    public Long getTimetableId() {
        return timetableId;
    }

    public void setTimetableId(Long timetableId) {
        this.timetableId = timetableId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public LocalDate getLectureDate() {
        return lectureDate;
    }

    public void setLectureDate(LocalDate lectureDate) {
        this.lectureDate = lectureDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getLectureType() {
        return lectureType;
    }

    public void setLectureType(String lectureType) {
        this.lectureType = lectureType;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getExpectedStudents() {
        return expectedStudents;
    }

    public void setExpectedStudents(long expectedStudents) {
        this.expectedStudents = expectedStudents;
    }

    public long getLikelyAbsentStudents() {
        return likelyAbsentStudents;
    }

    public void setLikelyAbsentStudents(long likelyAbsentStudents) {
        this.likelyAbsentStudents = likelyAbsentStudents;
    }

    public int getPresentCount() {
        return presentCount;
    }

    public void setPresentCount(int presentCount) {
        this.presentCount = presentCount;
    }

    public List<FeedbackSummaryDto> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<FeedbackSummaryDto> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public static class FeedbackSummaryDto {
        private String comment;
        private int rating;
        private String understand;

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public String getUnderstand() {
            return understand;
        }

        public void setUnderstand(String understand) {
            this.understand = understand;
        }
    }
}
