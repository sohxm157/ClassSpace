package com.classspace_backend.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.classspace_backend.demo.dto.LectureSlotDto;
import com.classspace_backend.demo.dto.StudentProfileDto;
import com.classspace_backend.demo.dto.StudentTimetableDto;
import com.classspace_backend.demo.dto.UpdateStudentProfileDto;
import com.classspace_backend.demo.entity.ClassMember;
import com.classspace_backend.demo.entity.Lecture;
import com.classspace_backend.demo.entity.Student;
import com.classspace_backend.demo.entity.Timetable;
import com.classspace_backend.demo.entity.User;
import com.classspace_backend.demo.repository.ClassMemberRepository;
import com.classspace_backend.demo.repository.LectureRepository;
import com.classspace_backend.demo.repository.StudentRepository;
import com.classspace_backend.demo.repository.TimetableRepository;
import com.classspace_backend.demo.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class StudentService {

        private final StudentRepository studentRepository;
        private final AuthService authService;
        private final UserRepository userRepository;
        private final ClassMemberRepository classMemberRepository;
        private final TimetableRepository timetableRepository;
        private final LectureRepository lectureRepository;
        private final com.classspace_backend.demo.repository.AnnouncementRepository announcementRepository;
        private final com.classspace_backend.demo.repository.IntegrityScoreRepository integrityScoreRepository;

        public StudentService(
                        StudentRepository studentRepository,
                        AuthService authService,
                        UserRepository userRepository,
                        ClassMemberRepository classMemberRepository,
                        TimetableRepository timetableRepository,
                        LectureRepository lectureRepository,
                        com.classspace_backend.demo.repository.AnnouncementRepository announcementRepository,
                        com.classspace_backend.demo.repository.IntegrityScoreRepository integrityScoreRepository) {
                this.studentRepository = studentRepository;
                this.authService = authService;
                this.userRepository = userRepository;
                this.classMemberRepository = classMemberRepository;
                this.timetableRepository = timetableRepository;
                this.lectureRepository = lectureRepository;
                this.announcementRepository = announcementRepository;
                this.integrityScoreRepository = integrityScoreRepository;
        }

        // =======================
        // PROFILE
        // =======================

        public StudentProfileDto getLoggedInStudentProfile(HttpServletRequest request) {

                User user = authService.getCurrentUserEntity();

                Student student = studentRepository
                                .findByUser(user)
                                .orElseThrow(() -> new RuntimeException("Student not found"));

                StudentProfileDto dto = new StudentProfileDto();
                dto.setName(user.getName());
                dto.setEmail(user.getEmail());
                dto.setPhone(user.getPhone());
                dto.setDob(user.getDob());
                dto.setAddress(user.getAddress());
                System.out.println(
                                "DEBUG: Fetching profile for " + user.getEmail() + " | Address: " + user.getAddress());
                dto.setPrn(student.getPrn());
                dto.setBranch(student.getBranch());
                dto.setDivision(student.getDivision());

                Optional<ClassMember> classMember = classMemberRepository.findTopByUserAndStatus(user, "APPROVED");

                classMember.ifPresent(cm -> dto.setClassName(cm.getClassEntity().getClassName()));

                // Integirty
                com.classspace_backend.demo.entity.IntegrityScore score = integrityScoreRepository
                                .findByStudent_UserId(user.getUserId()).orElse(null);
                if (score != null) {
                        dto.setCoins(score.getCoins());
                        dto.setIntegrityPercentage(score.getIntegrityPercentage());
                } else {
                        dto.setCoins(0);
                        dto.setIntegrityPercentage(java.math.BigDecimal.valueOf(100));
                }

                return dto;
        }

        public void updateProfile(
                        HttpServletRequest request,
                        UpdateStudentProfileDto dto) {

                User user = authService.getCurrentUserEntity();

                user.setEmail(dto.getEmail());
                user.setPhone(dto.getPhone());
                user.setDob(dto.getDob());
                user.setAddress(dto.getAddress());

                userRepository.save(user);
        }

        // =======================
        // STUDENT TIMETABLE (FIXED)
        // =======================

        public StudentTimetableDto getStudentTimetable(HttpServletRequest request) {

                User user = authService.getCurrentUserEntity();

                Student student = studentRepository
                                .findByUser(user)
                                .orElseThrow(() -> new RuntimeException("Student not found"));

                ClassMember classMember = classMemberRepository
                                .findTopByUserAndStatus(user, "APPROVED")
                                .orElse(null);

                if (classMember == null) {
                        StudentTimetableDto empty = new StudentTimetableDto();
                        empty.setClassName(null);
                        empty.setDivision(student.getDivision());
                        empty.setTimetable(Map.of());
                        return empty;
                }

                Long classId = classMember.getClassEntity().getClassId();

                List<Timetable> entries = timetableRepository.findByClassEntity_ClassIdAndDivision(
                                classId,
                                student.getDivision());

                LocalDate today = LocalDate.now();

                Map<String, List<LectureSlotDto>> grouped = entries.stream()
                                .collect(Collectors.groupingBy(
                                                t -> t.getDay().name(),
                                                Collectors.mapping(t -> {

                                                        LectureSlotDto slot = new LectureSlotDto();

                                                        // 🔥 MOST IMPORTANT LINE (BUG FIX)
                                                        slot.setTimetableId(t.getTimetableId());

                                                        slot.setStartTime(t.getStartTime().toString());
                                                        slot.setEndTime(t.getEndTime().toString());
                                                        slot.setSubject(t.getSubject());
                                                        slot.setTeacher(
                                                                        t.getTeacher() != null
                                                                                        ? t.getTeacher().getName()
                                                                                        : "TBA");

                                                        // Optional: existing lecture for today
                                                        Optional<Lecture> lectureOpt = lectureRepository
                                                                        .findByTimetable_TimetableIdAndLectureDate(
                                                                                        t.getTimetableId(),
                                                                                        today);

                                                        lectureOpt.ifPresent(
                                                                        lecture -> slot.setLectureId(
                                                                                        lecture.getLectureId()));

                                                        return slot;

                                                }, Collectors.toList())));

                StudentTimetableDto response = new StudentTimetableDto();
                response.setClassName(classMember.getClassEntity().getClassName());
                response.setDivision(student.getDivision());
                response.setTimetable(grouped);

                return response;
        }

        // =======================
        // RAW TIMETABLE LIST
        // =======================

        public List<Timetable> getMyTimetable(HttpServletRequest request) {

                User user = authService.getCurrentUserEntity();

                studentRepository.findByUser(user)
                                .orElseThrow(() -> new RuntimeException("Student not found"));

                ClassMember member = classMemberRepository
                                .findTopByUserAndStatus(user, "APPROVED")
                                .orElseThrow(() -> new RuntimeException("Class not joined"));

                return timetableRepository.findByClassEntity_ClassId(
                                member.getClassEntity().getClassId());
        }

        public java.util.List<com.classspace_backend.demo.entity.Announcement> getAnnouncements() {
                User user = authService.getCurrentUserEntity();
                Student student = studentRepository.findByUser(user)
                                .orElseThrow(() -> new RuntimeException("Student not found"));
                ClassMember member = classMemberRepository.findTopByUserAndStatus(user, "APPROVED")
                                .orElse(null);
                if (member == null)
                        return java.util.Collections.emptyList();

                return announcementRepository.findByClassEntity_ClassIdAndDivisionOrderByCreatedAtDesc(
                                member.getClassEntity().getClassId(), student.getDivision());
        }
}
