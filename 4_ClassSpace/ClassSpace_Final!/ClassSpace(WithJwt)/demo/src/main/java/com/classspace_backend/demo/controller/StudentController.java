package com.classspace_backend.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.classspace_backend.demo.dto.StudentProfileDto;
import com.classspace_backend.demo.dto.StudentTimetableDto;
import com.classspace_backend.demo.dto.UpdateStudentProfileDto;
import com.classspace_backend.demo.service.StudentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // ======================
    // PROFILE
    // ======================
    @GetMapping("/profile")
    public StudentProfileDto getProfile(HttpServletRequest request) {
        return studentService.getLoggedInStudentProfile(request);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            HttpServletRequest request,
            @Valid @RequestBody UpdateStudentProfileDto dto) {
        studentService.updateProfile(request, dto);
        return ResponseEntity.ok("Profile updated successfully");
    }

    // ======================
    // ✅ SINGLE TIMETABLE API
    // ======================
    @GetMapping("/timetable")
    public StudentTimetableDto getStudentTimetable(HttpServletRequest request) {
        return studentService.getStudentTimetable(request);
    }

    @GetMapping("/announcements")
    public ResponseEntity<java.util.List<com.classspace_backend.demo.entity.Announcement>> getAnnouncements() {
        return ResponseEntity.ok(studentService.getAnnouncements());
    }
}
