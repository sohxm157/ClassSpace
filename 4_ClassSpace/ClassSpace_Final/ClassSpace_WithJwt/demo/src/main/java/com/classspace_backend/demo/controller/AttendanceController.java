package com.classspace_backend.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.classspace_backend.demo.dto.AttendanceDTO;
import com.classspace_backend.demo.entity.ActualStatus;
import com.classspace_backend.demo.entity.DeclaredStatus;
import com.classspace_backend.demo.entity.IntegrityScore;
import com.classspace_backend.demo.entity.User;
import com.classspace_backend.demo.service.AttendanceService;
import com.classspace_backend.demo.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

        private final AttendanceService service;
        private final AuthService authService;

        public AttendanceController(
                        AttendanceService service,
                        AuthService authService) {
                this.service = service;
                this.authService = authService;
        }

        // ==============================
        // 1️⃣ STUDENT DECLARE ATTENDANCE
        // ==============================
        @PostMapping("/declare")
        public ResponseEntity<?> declare(
                        @RequestBody AttendanceDTO dto,
                        HttpServletRequest request) {
                try {
                        User student = authService.getCurrentUserEntity();
                        DeclaredStatus status = DeclaredStatus.valueOf(dto.status.toUpperCase());

                        service.declareAttendance(
                                        dto.lectureId,
                                        student.getUserId(),
                                        status);

                        return ResponseEntity.ok(
                                        java.util.Map.of("message", "Attendance updated successfully"));

                } catch (com.classspace_backend.demo.exception.InvalidOperationException e) {
                        return ResponseEntity
                                        .status(HttpStatus.BAD_REQUEST)
                                        .body(java.util.Map.of("message", e.getMessage()));
                } catch (IllegalArgumentException e) {
                        return ResponseEntity
                                        .badRequest()
                                        .body(java.util.Map.of("message", e.getMessage()));
                } catch (Exception e) {
                        return ResponseEntity
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(java.util.Map.of("message", "An error occurred: " + e.getMessage()));
                }
        }

        // ==============================
        // 2️⃣ GET STUDENT DECLARED STATUS
        // ==============================
        @GetMapping("/status/{lectureId}")
        public ResponseEntity<?> getDeclaredStatus(
                        @PathVariable Long lectureId,
                        HttpServletRequest request) {
                User student = authService.getCurrentUserEntity();

                DeclaredStatus status = service.getDeclaredStatus(
                                lectureId,
                                student.getUserId());

                // YES / NO / null
                return ResponseEntity.ok(status);
        }

        // ==============================
        // 3️⃣ COORDINATOR UPLOAD ACTUAL
        // ==============================
        @PostMapping("/actual")
        public ResponseEntity<?> uploadActual(
                        @RequestBody AttendanceDTO dto) {
                try {
                        ActualStatus status = ActualStatus.valueOf(dto.status.toUpperCase());

                        service.uploadActual(
                                        dto.lectureId,
                                        dto.studentId,
                                        status);

                        return ResponseEntity.ok("Actual attendance uploaded");

                } catch (Exception e) {
                        return ResponseEntity
                                        .status(HttpStatus.BAD_REQUEST)
                                        .body(e.getMessage());
                }
        }

        // ==============================
        // 4️⃣ EXCEL UPLOAD
        // ==============================
        @PostMapping("/actual/upload")
        public ResponseEntity<?> uploadExcel(
                        @RequestParam("file") MultipartFile file) {
                try {
                        service.uploadActualFromExcel(file);
                        return ResponseEntity.ok("Excel processed");

                } catch (Exception e) {
                        return ResponseEntity
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(e.getMessage());
                }
        }

        // ==============================
        // 5️⃣ INTEGRITY SCORE
        // ==============================
        @GetMapping("/integrity")
        public ResponseEntity<IntegrityScore> getIntegrity(
                        HttpServletRequest request) {
                User student = authService.getCurrentUserEntity();
                return ResponseEntity.ok(
                                service.getIntegrityScore(student.getUserId()));
        }
}
