package com.classspace_backend.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.classspace_backend.demo.dto.CreateTimetableSlotRequest;
import com.classspace_backend.demo.dto.TeacherTimetableDto;
import com.classspace_backend.demo.entity.Announcement;
import com.classspace_backend.demo.entity.Classes;
import com.classspace_backend.demo.entity.Lecture;
import com.classspace_backend.demo.entity.LectureStatus;
import com.classspace_backend.demo.entity.Note;
import com.classspace_backend.demo.entity.Teacher;
import com.classspace_backend.demo.entity.Timetable;
import com.classspace_backend.demo.entity.Timetable.Day;
import com.classspace_backend.demo.entity.User;
import com.classspace_backend.demo.exception.BadRequestException;
import com.classspace_backend.demo.exception.NotFoundException;
import com.classspace_backend.demo.exception.UnauthorizedException;
import com.classspace_backend.demo.repository.AnnouncementRepository;
import com.classspace_backend.demo.repository.ClassesRepository;
import com.classspace_backend.demo.repository.LectureRepository;
import com.classspace_backend.demo.repository.NotesRepository;
import com.classspace_backend.demo.repository.TeacherRepository;
import com.classspace_backend.demo.repository.TimetableRepository;
import com.classspace_backend.demo.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class TeacherService {

    private final TimetableRepository timetableRepository;
    private final LectureRepository lectureRepository;
    private final NotesRepository notesRepository;
    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final ClassesRepository classesRepository;
    private final TeacherRepository teacherRepository;

    public TeacherService(
            TimetableRepository timetableRepository,
            LectureRepository lectureRepository,
            NotesRepository notesRepository,
            AnnouncementRepository announcementRepository,
            UserRepository userRepository,
            AuthService authService,
            ClassesRepository classesRepository,
            TeacherRepository teacherRepository) {
        this.timetableRepository = timetableRepository;
        this.lectureRepository = lectureRepository;
        this.notesRepository = notesRepository;
        this.announcementRepository = announcementRepository;
        this.userRepository = userRepository;
        this.authService = authService;
        this.classesRepository = classesRepository;
        this.teacherRepository = teacherRepository;
    }

    // ==========================
    // 1️⃣ Teacher Dashboard
    // ==========================
    public List<TeacherTimetableDto> getTeacherTimetable(Authentication authentication) {
        String teacherName = authentication.getName();

        User teacher = (User) userRepository.findByEmail(teacherName).get();
        List<Timetable> entries = timetableRepository.findByTeacher_UserId(teacher.getUserId());

        return entries.stream().map(t -> {
            TeacherTimetableDto dto = new TeacherTimetableDto();
            dto.setTimetableId(t.getTimetableId());
            dto.setDay(t.getDay());
            dto.setStartTime(t.getStartTime());
            dto.setEndTime(t.getEndTime());
            dto.setSubject(t.getSubject());

            // Safety check to prevent the crash
            if (t.getClassEntity() != null) {
                dto.setClassName(t.getClassEntity().getClassName());
            } else {
                dto.setClassName("No Class Assigned");
            }

            return dto;
        }).sorted((a, b) -> {
            int dayCompare = a.getDay().ordinal() - b.getDay().ordinal();
            if (dayCompare != 0)
                return dayCompare;
            return a.getStartTime().compareTo(b.getStartTime());
        }).toList();
    }

    private int getDayOrder(String day) {
        return switch (day) {
            case "MON" -> 1;
            case "TUE" -> 2;
            case "WED" -> 3;
            case "THU" -> 4;
            case "FRI" -> 5;
            case "SAT" -> 6;
            default -> 7;
        };
    }

    // ==========================
    // 2️⃣ Create lecture instance (date-wise)
    // ==========================
    public Lecture createLecture(Long timetableId) {
        Lecture lecture = new Lecture();
        lecture.setTimetable(
                timetableRepository.findById(timetableId)
                        .orElseThrow(() -> new RuntimeException("Timetable not found")));
        lecture.setLectureDate(LocalDate.now());
        lecture.setStatus(LectureStatus.SCHEDULED);
        return lectureRepository.save(lecture);
    }

    // ==========================
    // 3️⃣ Cancel lecture
    // ==========================
    public void cancelLecture(Long lectureId, Long teacherId) {

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("Lecture not found"));

        // 1️⃣ Cancel lecture
        lecture.setStatus(LectureStatus.CANCELLED);
        lecture.setCancelledAt(LocalDateTime.now());
        lectureRepository.save(lecture);

        // 2️⃣ Get required entities (IMPORTANT PART)
        User teacher = lecture.getTimetable().getTeacher(); // ✅ User entity
        Classes classEntity = lecture.getTimetable().getClassEntity(); // ✅ Classes entity

        // 3️⃣ Create announcement (ENTITY SET KARO)
        Announcement ann = new Announcement();
        ann.setTitle("Lecture Cancelled");
        ann.setMessage("Today's lecture has been cancelled.");
        ann.setCreatedBy(teacher); // ✅ ENTITY
        ann.setClassEntity(classEntity); // ✅ ENTITY

        announcementRepository.save(ann);
    }

    // ==========================
    // 4️⃣ Upload notes (past lecture)
    // ==========================
    public void uploadNotes(
            Long lectureId,
            MultipartFile file,
            Long teacherId) {
        // 1️⃣ Get lecture
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("Lecture not found"));

        // 2️⃣ Get teacher (User entity)
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // 3️⃣ File upload logic (placeholder)
        String driveLink = "https://drive.fake/" + file.getOriginalFilename();

        // 4️⃣ Save note
        Note note = new Note();
        note.setLecture(lecture);
        note.setUploadedBy(teacher); // ✅ CORRECT
        note.setDriveLink(driveLink);
        note.setVersion(1);

        notesRepository.save(note);
    }

    @Transactional
    public ResponseEntity<?> createSlot(CreateTimetableSlotRequest req) {

        // 🔐 JWT user
        User teacher = authService.getCurrentUserEntity();

        if (!"TEACHER".equals(teacher.getRole().getRoleName())) {
            throw new UnauthorizedException("Only TEACHER can create timetable slots");
        }

        // 🧪 Validate
        if (req.getClassId() == null)
            throw new BadRequestException("classId required");
        if (req.getDay() == null)
            throw new BadRequestException("day required");
        if (req.getStartTime() == null)
            throw new BadRequestException("startTime required");
        if (req.getEndTime() == null)
            throw new BadRequestException("endTime required");
        if (req.getSubject() == null || req.getSubject().isBlank())
            throw new BadRequestException("subject required");

        Classes classEntity = classesRepository.findById(req.getClassId())
                .orElseThrow(() -> new NotFoundException("Class not found"));

        Day day;
        try {
            day = Timetable.Day.valueOf(req.getDay());
        } catch (Exception e) {
            throw new BadRequestException("Invalid day (MON–SAT)");
        }

        LocalTime start = LocalTime.parse(req.getStartTime());
        LocalTime end = LocalTime.parse(req.getEndTime());

        if (!end.isAfter(start)) {
            throw new BadRequestException("endTime must be after startTime");
        }

        Integer week = req.getWeekNumber() == null ? 1 : req.getWeekNumber();

        // 🚫 Duplicate slot check
        timetableRepository
                .findByClassEntityAndTeacherAndDayAndStartTimeAndEndTimeAndWeekNumberAndDivision(
                        classEntity,
                        teacher,
                        day,
                        start,
                        end,
                        week,
                        req.getDivision())
                .ifPresent(t -> {
                    throw new BadRequestException("Timetable slot already exists");
                });

        // ✅ Create slot
        Timetable slot = new Timetable();
        slot.setClassEntity(classEntity);
        slot.setTeacher(teacher);
        slot.setDay(day);
        slot.setWeekNumber(week);
        slot.setStartTime(start);
        slot.setEndTime(end);
        slot.setSubject(req.getSubject());
        slot.setDivision(req.getDivision());

        return ResponseEntity.ok(timetableRepository.save(slot));
    }

    public com.classspace_backend.demo.dto.TeacherProfileDto getTeacherProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Teacher not found"));

        java.util.Optional<Teacher> teacherOpt = teacherRepository.findByUser(user);

        com.classspace_backend.demo.dto.TeacherProfileDto dto = new com.classspace_backend.demo.dto.TeacherProfileDto();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setDob(user.getDob());
        dto.setAddress(user.getAddress());

        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            dto.setSubject(teacher.getSubject());
            dto.setAssignedClasses(teacher.getAssignedClasses());
            dto.setAssignedDivisions(teacher.getAssignedDivisions());
        }

        return dto;
    }
}
