package com.classspace_backend.demo.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.classspace_backend.demo.entity.Classes;
import com.classspace_backend.demo.entity.Timetable;
import com.classspace_backend.demo.entity.Timetable.Day;
import com.classspace_backend.demo.entity.User;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    // 🔹 Student / Class timetable (NO division here)
    List<Timetable> findByClassEntity_ClassId(Long classId);

    List<Timetable> findByClassEntity_ClassIdAndDivision(
            Long classId,
            String division);

    @Query("SELECT t FROM Timetable t JOIN FETCH t.classEntity WHERE t.teacher.userId = :teacherId")
    List<Timetable> findByTeacher_UserId(@Param("teacherId") Long teacherId);

    Optional<Timetable> findByClassEntityAndTeacherAndDayAndStartTimeAndEndTimeAndWeekNumberAndDivision(
            Classes classEntity,
            User teacher,
            Day day,
            LocalTime startTime,
            LocalTime endTime,
            Integer weekNumber,
            String division);

    @Query("SELECT DISTINCT t.division FROM Timetable t WHERE t.classEntity.classId = :classId")
    List<String> findDistinctDivisionsByClassId(@Param("classId") Long classId);
}
