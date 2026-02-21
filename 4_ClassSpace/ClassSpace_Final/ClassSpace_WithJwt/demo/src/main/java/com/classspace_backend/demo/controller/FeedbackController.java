package com.classspace_backend.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.classspace_backend.demo.dto.FeedbackDTO;
import com.classspace_backend.demo.dto.FeedbackResponseDTO;
import com.classspace_backend.demo.dto.FeedbackStatusDto;
import com.classspace_backend.demo.entity.User;
import com.classspace_backend.demo.exception.FeedbackAlreadySubmittedException;
import com.classspace_backend.demo.service.AuthService;
import com.classspace_backend.demo.service.FeedbackService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final AuthService authService;

    public FeedbackController(
            FeedbackService feedbackService,
            AuthService authService) {
        this.feedbackService = feedbackService;
        this.authService = authService;
    }

    // ===============================
    // 1️⃣ SUBMIT FEEDBACK (STUDENT)
    // ===============================
    @PostMapping("/submit")
    public ResponseEntity<?> submitFeedback(@RequestBody FeedbackDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("AUTH = " + auth);
        System.out.println("AUTHORITIES = " + auth.getAuthorities());
        feedbackService.submitFeedback(dto);
        return ResponseEntity.ok("Feedback submitted successfully");
    }

    // ===============================
    // 2️⃣ TEACHER VIEW (LECTURE)
    // ===============================
    @GetMapping("/lecture/{lectureId}")
    public ResponseEntity<FeedbackResponseDTO> getTeacherLectureFeedback(
            @PathVariable Long lectureId) {
        return ResponseEntity.ok(
                feedbackService.getTeacherLectureFeedback(lectureId));
    }

    // ===============================
    // 3️⃣ STUDENT FEEDBACK STATUS ✅ FIXED
    // ===============================
    @GetMapping("/status/{lectureId}")
    public ResponseEntity<FeedbackStatusDto> getFeedbackStatus(
            @PathVariable Long lectureId,
            HttpServletRequest request) {
        User user = authService.getCurrentUserEntity(); // ✅ SIMPLE

        return ResponseEntity.ok(
                feedbackService.getFeedbackStatus(lectureId, user));
    }

    // ===============================
    // 🔥 EXCEPTION HANDLERS
    // ===============================
    @ExceptionHandler(FeedbackAlreadySubmittedException.class)
    public ResponseEntity<?> handleFeedbackAlreadySubmitted(FeedbackAlreadySubmittedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(java.util.Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(java.util.Map.of("message", ex.getMessage()));
    }
}
