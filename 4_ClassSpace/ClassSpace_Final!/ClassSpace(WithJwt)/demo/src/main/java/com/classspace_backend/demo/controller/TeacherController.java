package com.classspace_backend.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.classspace_backend.demo.dto.CreateTimetableSlotRequest;
import com.classspace_backend.demo.dto.TeacherProfileDto;
import com.classspace_backend.demo.dto.TeacherTimetableDto;
import com.classspace_backend.demo.entity.Lecture;
import com.classspace_backend.demo.entity.User;
import com.classspace_backend.demo.repository.UserRepository;
import com.classspace_backend.demo.service.TeacherService;

@RestController
@RequestMapping("/api/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {

    private final TeacherService teacherService;
    private final UserRepository userRepository;

    public TeacherController(TeacherService teacherService, UserRepository userRepository) {
        this.teacherService = teacherService;
        this.userRepository = userRepository;
    }

    // ==========================
    // 1️⃣ Teacher Dashboard
    // ==========================
    @GetMapping("/dashboard")
    public ResponseEntity<List<TeacherTimetableDto>> dashboard(
            Authentication authentication) {

        return ResponseEntity.ok(
                teacherService.getTeacherTimetable(authentication));
    }

    // ==========================
    // 2️⃣ Create lecture (date-wise)
    // ==========================
    @PostMapping("/lecture/create/{timetableId}")
    public ResponseEntity<Lecture> createLecture(
            @PathVariable Long timetableId) {
        return ResponseEntity.ok(
                teacherService.createLecture(timetableId));
    }

    // ==========================
    // 3️⃣ Cancel lecture
    // ==========================
    @PutMapping("/lecture/{lectureId}/cancel")
    public ResponseEntity<String> cancelLecture(
            @PathVariable Long lectureId,
            @AuthenticationPrincipal User teacher) {
        teacherService.cancelLecture(lectureId, teacher.getUserId());
        return ResponseEntity.ok("Lecture cancelled successfully");
    }

    // ==========================
    // 4️⃣ Upload notes
    // ==========================
    @PostMapping("/lecture/{lectureId}/notes")
    public ResponseEntity<String> uploadNotes(
            @PathVariable Long lectureId,
            @RequestParam MultipartFile file,
            @AuthenticationPrincipal User teacher) {
        teacherService.uploadNotes(
                lectureId,
                file,
                teacher.getUserId());
        return ResponseEntity.ok("Notes uploaded successfully");
    }

    @GetMapping("/timetable")
    public ResponseEntity<?> getTeacherTimetable(Authentication authentication) {
        System.out.println(">>> ENTERING TIMETABLE CONTROLLER");
        try {

            // Now use 'devUser' for your logic instead of authentication.getPrincipal()
            return ResponseEntity.ok(teacherService.getTeacherTimetable(authentication));
        } catch (Exception e) {
            e.printStackTrace(); // This WILL force the error into your console
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/timetable/slot")
    public ResponseEntity<?> createSlot(
            @RequestBody CreateTimetableSlotRequest req) {
        return teacherService.createSlot(req);
    }

    @GetMapping("/profile")
    public ResponseEntity<TeacherProfileDto> getProfile(Authentication authentication) {
        return ResponseEntity.ok(teacherService.getTeacherProfile(authentication));
    }
}
