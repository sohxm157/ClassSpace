package com.classspace_backend.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.classspace_backend.demo.dto.LectureDetailsDto;
import com.classspace_backend.demo.dto.ResolveLectureRequest;
import com.classspace_backend.demo.entity.Lecture;
import com.classspace_backend.demo.entity.Timetable;
import com.classspace_backend.demo.exception.ResourceNotFoundException;
import com.classspace_backend.demo.repository.AttendanceDeclaredRepository;
import com.classspace_backend.demo.repository.LectureRepository;
import com.classspace_backend.demo.repository.StudentRepository;
import com.classspace_backend.demo.repository.TimetableRepository;

@Service
public class LectureService {

        private final LectureRepository lectureRepository;
        private final TimetableRepository timetableRepository;
        private final AttendanceDeclaredRepository attendanceDeclaredRepository;
        private final StudentRepository studentRepository;
        private final com.classspace_backend.demo.repository.AnnouncementRepository announcementRepository;
        private final com.classspace_backend.demo.repository.FeedbackRepository feedbackRepository;
        private final com.classspace_backend.demo.repository.AttendanceActualRepository attendanceActualRepository;

        public LectureService(
                        LectureRepository lectureRepository,
                        TimetableRepository timetableRepository,
                        AttendanceDeclaredRepository attendanceDeclaredRepository,
                        StudentRepository studentRepository,
                        com.classspace_backend.demo.repository.AnnouncementRepository announcementRepository,
                        com.classspace_backend.demo.repository.FeedbackRepository feedbackRepository,
                        com.classspace_backend.demo.repository.AttendanceActualRepository attendanceActualRepository) {
                this.lectureRepository = lectureRepository;
                this.timetableRepository = timetableRepository;
                this.attendanceDeclaredRepository = attendanceDeclaredRepository;
                this.studentRepository = studentRepository;
                this.announcementRepository = announcementRepository;
                this.feedbackRepository = feedbackRepository;
                this.attendanceActualRepository = attendanceActualRepository;
        }

        /**
         * Resolve lecture ONLY for opening lecture page.
         * If timetable slot is visible, lecture must open.
         * NO date/day validation here.
         */
        @Transactional
        public LectureDetailsDto resolveLecture(ResolveLectureRequest req) {

                if (req.getTimetableId() == null) {
                        throw new IllegalArgumentException("timetableId is required");
                }

                // Date Logic: If provided, use it.
                // If NOT provided, calculate based on Timetable Day for the CURRENT WEEK.
                // This ensures "Monday slot" resolved on "Tuesday" is still saved as "Monday's
                // Date".
                LocalDate date;
                if (req.getDate() != null) {
                        date = req.getDate();
                } else {
                        Timetable t = timetableRepository.findById(req.getTimetableId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Timetable not found for date calculation"));

                        LocalDate today = LocalDate.now();
                        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1); // Monday
                        int dayOffset = t.getDay().ordinal(); // Assuming Enum MON=0, TUE=1...
                        date = startOfWeek.plusDays(dayOffset);
                }

                Timetable timetable = timetableRepository.findById(req.getTimetableId())
                                .orElseThrow(() -> new ResourceNotFoundException("Timetable not found"));

                Lecture lecture;

                try {
                        lecture = lectureRepository
                                        .findByTimetable_TimetableIdAndLectureDate(
                                                        timetable.getTimetableId(), date)
                                        .orElseGet(() -> {
                                                Lecture l = new Lecture();
                                                l.setTimetable(timetable);
                                                l.setLectureDate(date);
                                                return lectureRepository.save(l);
                                        });

                } catch (DataIntegrityViolationException ex) {
                        // Race condition safe
                        lecture = lectureRepository
                                        .findByTimetable_TimetableIdAndLectureDate(
                                                        timetable.getTimetableId(), date)
                                        .orElseThrow(() -> new IllegalStateException(
                                                        "Lecture exists but could not be retrieved"));
                }

                // ⚠️ OPTIONAL: Validate day-of-week matches timetable day
                java.time.DayOfWeek actualDay = date.getDayOfWeek();
                String timetableDay = timetable.getDay().name();
                String actualDayStr = actualDay.name().substring(0, 3); // MON, TUE, etc.

                if (!timetableDay.equals(actualDayStr)) {
                        System.out.println("⚠️ WARNING: Lecture date " + date + " (" + actualDayStr +
                                        ") does not match timetable day (" + timetableDay + ")");
                }

                return prepareLectureDetailsDto(lecture);
        }

        public LectureDetailsDto getLectureDetails(Long lectureId) {
                Lecture lecture = lectureRepository.findById(lectureId)
                                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found"));
                return prepareLectureDetailsDto(lecture);
        }

        private LectureDetailsDto prepareLectureDetailsDto(Lecture lecture) {
                Timetable timetable = lecture.getTimetable();
                LectureDetailsDto dto = new LectureDetailsDto();

                dto.setLectureId(lecture.getLectureId());
                dto.setTimetableId(timetable.getTimetableId());
                dto.setSubject(timetable.getSubject());
                dto.setClassName(null); // Or fetch from timetable if available
                dto.setLectureDate(lecture.getLectureDate());
                dto.setStartTime(timetable.getStartTime());
                dto.setEndTime(timetable.getEndTime());
                dto.setStatus(lecture.getStatus().name());
                dto.setStartedAt(lecture.getStartedAt());
                dto.setEndedAt(lecture.getEndedAt());
                dto.setCancelledAt(lecture.getCancelledAt());

                LocalDateTime lectureEnd = LocalDateTime.of(lecture.getLectureDate(), timetable.getEndTime());

                dto.setLectureType(
                                LocalDateTime.now().isBefore(lectureEnd) ? "FUTURE" : "PAST");

                // ===============================
                // FEEDBACKS
                // ===============================
                List<com.classspace_backend.demo.entity.Feedback> fbList = feedbackRepository
                                .findByLecture_LectureId(lecture.getLectureId());
                List<LectureDetailsDto.FeedbackSummaryDto> summaries = fbList.stream().map(f -> {
                        LectureDetailsDto.FeedbackSummaryDto s = new LectureDetailsDto.FeedbackSummaryDto();
                        s.setComment(f.getComment());
                        s.setRating(f.getStarRating() != null ? f.getStarRating() : 0);
                        s.setUnderstand(f.getUnderstand() != null ? f.getUnderstand().name() : "YES");
                        return s;
                }).collect(java.util.stream.Collectors.toList());
                dto.setFeedbacks(summaries);

                // ===============================
                // ATTENDANCE STATS
                // ===============================
                String division = timetable.getDivision();
                List<com.classspace_backend.demo.entity.Student> divisionStudents = studentRepository
                                .findByDivisionAndUser_IsActiveTrue(division);

                long totalStudents = divisionStudents.size();

                List<com.classspace_backend.demo.entity.AttendanceDeclared> declared = attendanceDeclaredRepository
                                .findByLecture_LectureId(lecture.getLectureId());

                long expectedCount = declared.stream()
                                .filter(d -> d.getDeclaredStatus() == com.classspace_backend.demo.entity.DeclaredStatus.YES)
                                .count();

                // Actual attendance
                long presentCount = attendanceActualRepository.findByLecture_LectureId(lecture.getLectureId()).stream()
                                .filter(a -> a.getActualStatus() == com.classspace_backend.demo.entity.ActualStatus.PRESENT)
                                .count();

                dto.setTotalStudents(totalStudents);
                dto.setExpectedStudents(expectedCount);
                dto.setLikelyAbsentStudents(totalStudents - expectedCount);
                dto.setPresentCount((int) presentCount);

                return dto;
        }

        @Transactional
        public void cancelLecture(Long lectureId, String reason) {
                Lecture lecture = lectureRepository.findById(lectureId)
                                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found"));

                if (lecture.getStatus() == com.classspace_backend.demo.entity.LectureStatus.CANCELLED) {
                        throw new IllegalStateException("Lecture is already cancelled");
                }

                lecture.setStatus(com.classspace_backend.demo.entity.LectureStatus.CANCELLED);
                lecture.setCancelledAt(LocalDateTime.now());
                lectureRepository.save(lecture);

                // Create Announcement for Students
                Timetable timetable = lecture.getTimetable();
                com.classspace_backend.demo.entity.Announcement announcement = new com.classspace_backend.demo.entity.Announcement();
                announcement.setTitle("LECTURE CANCELLED: " + timetable.getSubject());
                announcement.setMessage("The lecture scheduled for today (" + lecture.getLectureDate() + ") at "
                                + timetable.getStartTime() + " has been cancelled. Reason: " + reason);
                announcement.setCreatedBy(timetable.getTeacher());
                announcement.setClassEntity(timetable.getClassEntity());
                announcement.setDivision(timetable.getDivision());
                announcement.setCreatedAt(LocalDateTime.now());
                announcementRepository.save(announcement);
        }
}
