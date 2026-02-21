package com.classspace_backend.demo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.classspace_backend.demo.entity.Lecture;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {

    Optional<Lecture> findByTimetable_TimetableIdAndLectureDate(
        Long timetableId,
        LocalDate lectureDate
    );

    List<Lecture> findByTimetable_Teacher_UserId(Long teacherId);
    
    
}
