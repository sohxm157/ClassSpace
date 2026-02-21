package com.classspace_backend.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.classspace_backend.demo.dto.FeedbackDTO;
import com.classspace_backend.demo.dto.FeedbackResponseDTO;
import com.classspace_backend.demo.dto.FeedbackStatusDto;
import com.classspace_backend.demo.entity.Feedback;
import com.classspace_backend.demo.entity.Lecture;
import com.classspace_backend.demo.entity.User;
import com.classspace_backend.demo.exception.ResourceNotFoundException;
import com.classspace_backend.demo.repository.FeedbackRepository;
import com.classspace_backend.demo.repository.LectureRepository;
import com.classspace_backend.demo.util.GenerateHash;

@Service
public class FeedbackService {

        private final FeedbackRepository feedbackRepository;
        private final LectureRepository lectureRepository;

        public FeedbackService(
                        FeedbackRepository feedbackRepository,
                        LectureRepository lectureRepository) {
                this.feedbackRepository = feedbackRepository;
                this.lectureRepository = lectureRepository;
        }

        // ===============================
        // 1️⃣ SUBMIT FEEDBACK (STUDENT)
        // ===============================
        public void submitFeedback(FeedbackDTO dto) {

                Lecture lecture = lectureRepository.findById(dto.getLectureId())
                                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));

                // ⏰ ADD HERE
                LocalDateTime lectureEndTime = LocalDateTime.of(
                                lecture.getLectureDate(),
                                lecture.getTimetable().getEndTime());

                if (LocalDateTime.now().isBefore(lectureEndTime)) {
                        throw new IllegalArgumentException(
                                        "Feedback can be submitted only after the lecture ends");
                }

                // 🔐 hash + already submitted check
                String hashedKey = GenerateHash.generateHash(
                                dto.getStudentId() + "_" + dto.getLectureId());

                boolean alreadyExists = feedbackRepository.existsByLecture_LectureIdAndHashedStudentKey(
                                dto.getLectureId(),
                                hashedKey);

                if (alreadyExists) {
                        throw new com.classspace_backend.demo.exception.FeedbackAlreadySubmittedException(
                                        "Feedback already submitted for this lecture");
                }

                Feedback feedback = new Feedback();
                feedback.setLecture(lecture);
                feedback.setHashedStudentKey(hashedKey);
                feedback.setUnderstand(Feedback.Understand.valueOf(dto.getUnderstand()));
                feedback.setStarRating(dto.getStarRating());
                feedback.setComment(dto.getComment());

                feedbackRepository.save(feedback);
        }

        // ===============================
        // 2️⃣ TEACHER VIEW (LECTURE)
        // ===============================
        public List<String> getLectureComments(Long lectureId) {
                return feedbackRepository.findCommentsByLecture(lectureId);
        }

        // ===============================
        // 3️⃣ AGGREGATION (STARS + RATIO)
        // ===============================
        public List<Feedback> getLectureFeedbackStats(Long lectureId) {
                return feedbackRepository.findByLecture_LectureId(lectureId);
        }

        public FeedbackStatusDto getFeedbackStatus(Long lectureId, User user) {

                Lecture lecture = lectureRepository.findById(lectureId)
                                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found"));

                // ⏰ lecture ended?
                LocalDateTime lectureEndTime = LocalDateTime.of(
                                lecture.getLectureDate(),
                                lecture.getTimetable().getEndTime());

                boolean allowed = !LocalDateTime.now().isBefore(lectureEndTime);

                // 🔐 already submitted?
                String hashedKey = GenerateHash.generateHash(
                                user.getUserId() + "_" + lectureId);

                boolean submitted = feedbackRepository.existsByLecture_LectureIdAndHashedStudentKey(
                                lectureId,
                                hashedKey);

                return new FeedbackStatusDto(submitted, allowed);
        }

        public FeedbackResponseDTO getTeacherLectureFeedback(Long lectureId) {

                List<Feedback> all = feedbackRepository.findByLecture_LectureId(lectureId);

                long understood = all.stream()
                                .filter(f -> f.getUnderstand() == Feedback.Understand.YES)
                                .count();

                long notUnderstood = all.size() - understood;

                double avgStars = all.stream()
                                .filter(f -> f.getStarRating() != null)
                                .mapToInt(Feedback::getStarRating)
                                .average()
                                .orElse(0.0);

                List<FeedbackDTO> items = all.stream()
                                .filter(f -> f.getComment() != null && !f.getComment().trim().isEmpty())
                                .map(f -> new FeedbackDTO(
                                                f.getStarRating(),
                                                f.getUnderstand() == null ? null : f.getUnderstand().name(),
                                                f.getComment()))
                                .collect(Collectors.toList());

                return new FeedbackResponseDTO(avgStars, understood, notUnderstood, items);
        }

}
