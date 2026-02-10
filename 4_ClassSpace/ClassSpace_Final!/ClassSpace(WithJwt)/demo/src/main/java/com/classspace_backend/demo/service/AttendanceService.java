package com.classspace_backend.demo.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.classspace_backend.demo.entity.*;
import com.classspace_backend.demo.repository.*;
import com.classspace_backend.demo.exception.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Iterator;

@Service
public class AttendanceService {

    private final AttendanceDeclaredRepository declaredRepo;
    private final AttendanceActualRepository actualRepo;
    private final IntegrityScoreRepository integrityRepo;
    private final LectureRepository lectureRepo;
    private final UserRepository userRepo;

    public AttendanceService(
            AttendanceDeclaredRepository declaredRepo,
            AttendanceActualRepository actualRepo,
            IntegrityScoreRepository integrityRepo,
            LectureRepository lectureRepo,
            UserRepository userRepo) {
        this.declaredRepo = declaredRepo;
        this.actualRepo = actualRepo;
        this.integrityRepo = integrityRepo;
        this.lectureRepo = lectureRepo;
        this.userRepo = userRepo;
    }

    // ===============================
    // 1️⃣ STUDENT POLL
    // ===============================
    public void declareAttendance(Long lectureId, Long studentId, DeclaredStatus newStatus) {

        Lecture lecture = lectureRepo.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found"));

        User student = userRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        LocalDateTime lectureStart = LocalDateTime.of(
                lecture.getLectureDate(),
                lecture.getTimetable().getStartTime());
        LocalDateTime oneHourBefore = lectureStart.minusHours(1);
        LocalDateTime now = LocalDateTime.now();

        // Get or create attendance record
        AttendanceDeclared ad = declaredRepo.findByLecture_LectureIdAndStudent_UserId(lectureId, studentId)
                .orElseGet(() -> {
                    AttendanceDeclared fresh = new AttendanceDeclared();
                    fresh.setLecture(lecture);
                    fresh.setStudent(student);
                    fresh.setDeclaredStatus(DeclaredStatus.YES); // ✅ DEFAULT PRESENT
                    return fresh;
                });

        DeclaredStatus oldStatus = ad.getDeclaredStatus();

        // 🔴 AFTER lecture starts - NO CHANGES ALLOWED
        if (now.isAfter(lectureStart) || now.isEqual(lectureStart)) {
            throw new InvalidOperationException("Attendance is locked after lecture starts");
        }

        // 🟡 BETWEEN (1 hour before → lecture start)
        if (now.isAfter(oneHourBefore) && now.isBefore(lectureStart)) {
            // ❌ PRESENT → ABSENT not allowed
            if (oldStatus == DeclaredStatus.YES && newStatus == DeclaredStatus.NO) {
                throw new InvalidOperationException(
                        "Cannot mark absent within 1 hour of lecture start");
            }
            // ✅ ABSENT → PRESENT allowed
        }

        // 🟢 BEFORE (More than 1 hour before)
        // ✅ All changes allowed - no restrictions

        ad.setDeclaredStatus(newStatus);
        declaredRepo.save(ad);
    }

    // =====================================
    // 2️⃣ COORDINATOR UPLOAD (API / Excel)
    // =====================================
    @Transactional
    public void uploadActual(Long lectureId, Long studentId, ActualStatus actual) {

        Lecture lecture = lectureRepo.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found"));

        User student = userRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // ---------- SAFE-GUARD: avoid duplicate processing ----------
        final boolean[] isNewAttendance = { false };

        AttendanceActual aa = actualRepo
                .findByLecture_LectureIdAndStudent_UserId(lectureId, studentId)
                .orElseGet(() -> {
                    AttendanceActual newAa = new AttendanceActual();
                    newAa.setLecture(lecture);
                    newAa.setStudent(student);
                    isNewAttendance[0] = true;
                    return newAa;
                });

        aa.setActualStatus(actual);
        actualRepo.save(aa);

        // ---------- DECLARED STATUS ----------
        DeclaredStatus declared = declaredRepo
                .findByLecture_LectureIdAndStudent_UserId(lectureId, studentId)
                .map(AttendanceDeclared::getDeclaredStatus)
                .orElse(DeclaredStatus.YES);

        // ---------- COINS SYSTEM ----------
        IntegrityScore score = integrityRepo
                .findByStudent_UserId(studentId)
                .orElseGet(() -> {
                    IntegrityScore s = new IntegrityScore();
                    s.setStudent(student);
                    s.setCoins(0); // 🪙 INITIAL COINS
                    return s;
                });

        // 🔒 Apply coins ONLY ONCE per lecture
        if (isNewAttendance[0]) {

            int coins = score.getCoins();
            int honest = score.getHonestCount();
            int dishonest = score.getDishonestCount();
            int total = score.getTotalLectures() + 1;

            if (declared == DeclaredStatus.YES && actual == ActualStatus.PRESENT) {
                coins += 10;
                honest++;
            } else if (declared == DeclaredStatus.YES && actual == ActualStatus.ABSENT) {
                coins -= 10;
                dishonest++;
            } else if (declared == DeclaredStatus.NO && actual == ActualStatus.PRESENT) {
                // coins += 0;
            } else if (declared == DeclaredStatus.NO && actual == ActualStatus.ABSENT) {
                coins += 5;
                honest++;
            }

            score.setCoins(coins);
            score.setHonestCount(honest);
            score.setDishonestCount(dishonest);
            score.setTotalLectures(total);

            // Calculate percentage: (Honest / Total) * 100
            if (total > 0) {
                double pct = (double) honest / total * 100.0;
                score.setIntegrityPercentage(java.math.BigDecimal.valueOf(pct));
            }
        }

        integrityRepo.save(score);
    }

    // ===============================
    // 3️⃣ GET STUDENT COINS
    // ===============================
    public IntegrityScore getIntegrityScore(Long studentId) {
        return integrityRepo.findByStudent_UserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Coins not found for studentId: " + studentId));
    }

    // ===============================
    // 4️⃣ EXCEL UPLOAD
    // ===============================
    @Transactional
    public void uploadActualFromExcel(MultipartFile file) throws Exception {

        try (InputStream is = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            while (rows.hasNext()) {
                Row row = rows.next();

                if (row.getRowNum() == 0)
                    continue;

                Cell lectureCell = row.getCell(0);
                Cell studentCell = row.getCell(1);
                Cell statusCell = row.getCell(2);

                if (lectureCell == null || studentCell == null || statusCell == null)
                    continue;

                Long lectureId = (long) lectureCell.getNumericCellValue();
                Long studentId = (long) studentCell.getNumericCellValue();
                ActualStatus status = ActualStatus.valueOf(statusCell.getStringCellValue().toUpperCase());

                uploadActual(lectureId, studentId, status);
            }
        } catch (Exception e) {
            throw new Exception("Error reading Excel file: " + e.getMessage());
        }
    }

    public DeclaredStatus getDeclaredStatus(Long lectureId, Long studentId) {

        return declaredRepo
                .findByLecture_LectureIdAndStudent_UserId(lectureId, studentId)
                .map(AttendanceDeclared::getDeclaredStatus)
                .orElse(DeclaredStatus.YES); // default PRESENT
    }

}
