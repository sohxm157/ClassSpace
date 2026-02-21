package com.classspace_backend.demo.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.classspace_backend.demo.dto.AddStudentDto;
import com.classspace_backend.demo.dto.AddTeacherDto;
import com.classspace_backend.demo.dto.CoordinatorAnnouncementDto;
import com.classspace_backend.demo.dto.CreateTimetableSlotRequest;
import com.classspace_backend.demo.entity.Announcement;
import com.classspace_backend.demo.entity.Classes;
import com.classspace_backend.demo.entity.Student;
import com.classspace_backend.demo.entity.Teacher;
import com.classspace_backend.demo.entity.Timetable;
import com.classspace_backend.demo.service.CoordinatorService;

@RestController
@RequestMapping("/api/coordinator")
@PreAuthorize("hasRole('COORDINATOR')")
public class CoordinatorController {

    private final CoordinatorService coordinatorService;

    public CoordinatorController(CoordinatorService coordinatorService) {
        this.coordinatorService = coordinatorService;
    }

    // 1. Student Management
    @PostMapping("/student/add")
    public ResponseEntity<Student> addStudent(@RequestBody AddStudentDto dto) {
        return ResponseEntity.ok(coordinatorService.addStudentOneToOne(dto));
    }

    @PostMapping("/student/upload-bulk")
    public ResponseEntity<?> uploadStudents(@RequestParam("file") MultipartFile file) {
        byte[] report = coordinatorService.addStudentsFromExcel(file);
        if (report == null) {
            return ResponseEntity.ok("Bulk upload successful");
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=failed_students.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(report);
    }

    @GetMapping("/student/template")
    public ResponseEntity<byte[]> getTemplate() {
        byte[] template = coordinatorService.getStudentUploadTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_template.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(template);
    }

    @DeleteMapping("/student/{studentId}")
    public ResponseEntity<String> removeStudent(@PathVariable Long studentId) {
        coordinatorService.removeStudent(studentId);
        return ResponseEntity.ok("Student removed successfully");
    }

    @GetMapping("/students/search")
    public ResponseEntity<List<Student>> searchStudents(@RequestParam String query) {
        return ResponseEntity.ok(coordinatorService.searchStudents(query));
    }

    // 2. Teacher Management
    @PostMapping("/teacher/add")
    public ResponseEntity<Teacher> addTeacher(@RequestBody AddTeacherDto dto) {
        return ResponseEntity.ok(coordinatorService.addTeacher(dto));
    }

    @DeleteMapping("/teacher/{teacherId}")
    public ResponseEntity<String> removeTeacher(@PathVariable Long teacherId) {
        coordinatorService.removeTeacher(teacherId);
        return ResponseEntity.ok("Teacher access revoked");
    }

    // 3. Class & Division
    @GetMapping("/classes")
    public ResponseEntity<List<Classes>> getAllClasses() {
        return ResponseEntity.ok(coordinatorService.getAllClasses());
    }

    @GetMapping("/classes/{classId}/divisions")
    public ResponseEntity<List<String>> getDivisions(@PathVariable Long classId) {
        // Just return dummy or actual divisions
        return ResponseEntity.ok(coordinatorService.getDivisions(classId));
    }

    @GetMapping("/division/{division}/students")
    public ResponseEntity<List<Student>> getStudentsByDivision(@PathVariable String division) {
        return ResponseEntity.ok(coordinatorService.getStudentsByDivision(division));
    }

    @GetMapping("/timetable/{classId}/{division}")
    public ResponseEntity<List<com.classspace_backend.demo.dto.CoordinatorTimetableDto>> getTimetable(
            @PathVariable Long classId,
            @PathVariable String division) {
        return ResponseEntity.ok(coordinatorService.getTimetable(classId, division));
    }

    // 4. Lecture Control
    @PostMapping("/lecture/{lectureId}/cancel")
    public ResponseEntity<String> cancelLecture(@PathVariable Long lectureId) {
        coordinatorService.cancelLecture(lectureId);
        return ResponseEntity.ok("Lecture cancelled and announcement created");
    }

    // 5. Announcements
    @PostMapping("/announcement/create")
    public ResponseEntity<Announcement> createAnnouncement(@RequestBody CoordinatorAnnouncementDto dto) {
        return ResponseEntity.ok(coordinatorService.createAnnouncement(dto));
    }

    // 6. Timetable & Teacher Selection
    @PostMapping("/timetable/slot")
    public ResponseEntity<Timetable> createTimetableSlot(@RequestBody CreateTimetableSlotRequest req) {
        return ResponseEntity.ok(coordinatorService.createTimetableSlot(req));
    }

    @DeleteMapping("/timetable/slot/{slotId}")
    public ResponseEntity<String> deleteTimetableSlot(@PathVariable Long slotId) {
        coordinatorService.deleteTimetableSlot(slotId);
        return ResponseEntity.ok("Timetable slot deleted successfully");
    }

    @GetMapping("/teachers")
    public ResponseEntity<List<Teacher>> getAllTeachers() {
        return ResponseEntity.ok(coordinatorService.getAllTeachers());
    }

    // 7. Bulk Timetable Upload
    @GetMapping("/timetable/template")
    public ResponseEntity<byte[]> getTimetableTemplate() {
        byte[] template = coordinatorService.getTimetableTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=timetable_template.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(template);
    }

    @PostMapping("/timetable/upload-bulk")
    public ResponseEntity<?> uploadTimetable(@RequestParam("file") MultipartFile file) {
        byte[] report = coordinatorService.addTimetableFromExcel(file);
        if (report == null) {
            return ResponseEntity.ok("Timetable upload successful");
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=failed_timetable_slots.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(report);
    }

    @GetMapping("/lecture/{lectureId}/attendance-template")
    public ResponseEntity<byte[]> getLectureAttendanceTemplate(@PathVariable Long lectureId) {
        byte[] template = coordinatorService.getLectureAttendanceTemplate(lectureId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=attendance_template_" + lectureId + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(template);
    }

    @PostMapping("/lecture/{lectureId}/attendance-upload")
    public ResponseEntity<String> uploadLectureAttendance(@PathVariable Long lectureId,
            @RequestParam("file") MultipartFile file) {
        coordinatorService.uploadLectureAttendanceExcel(lectureId, file);
        return ResponseEntity.ok("Attendance processed successfully");
    }
}
