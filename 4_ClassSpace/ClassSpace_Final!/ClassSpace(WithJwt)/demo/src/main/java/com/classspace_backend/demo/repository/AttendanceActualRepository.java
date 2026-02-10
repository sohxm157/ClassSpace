package com.classspace_backend.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.classspace_backend.demo.entity.AttendanceActual;

@Repository
public interface AttendanceActualRepository
        extends JpaRepository<AttendanceActual, Long> {

    List<AttendanceActual> findByLecture_LectureId(Long lectureId);

    Optional<AttendanceActual>
        findByLecture_LectureIdAndStudent_UserId(
            Long lectureId,
            Long studentId
        );
}
