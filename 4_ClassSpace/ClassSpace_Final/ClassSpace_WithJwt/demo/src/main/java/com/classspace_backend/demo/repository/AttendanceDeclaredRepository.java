package com.classspace_backend.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.classspace_backend.demo.entity.AttendanceDeclared;

@Repository
public interface AttendanceDeclaredRepository
        extends JpaRepository<AttendanceDeclared, Long> {

    // 🔹 Get all declared attendance for a lecture
    List<AttendanceDeclared> findByLecture_LectureId(Long lectureId);

    // 🔹 Get declared attendance for a specific student in a lecture
    Optional<AttendanceDeclared>
        findByLecture_LectureIdAndStudent_UserId(
            Long lectureId,
            Long studentId
        );
}
