package com.classspace_backend.demo.dto;

import com.classspace_backend.demo.entity.Timetable;

public class CoordinatorTimetableDto {
    private Long timetableId;
    private String subject;
    private String day;
    private String startTime;
    private String endTime;
    private String teacherName;
    private String division;
    private Long lectureId;
    private String status; // SCHEDULED, CANCELLED, COMPLETED

    public CoordinatorTimetableDto(Timetable t, Long lectureId, String status) {
        this.timetableId = t.getTimetableId();
        this.subject = t.getSubject();
        this.day = t.getDay().toString();
        this.startTime = t.getStartTime().toString();
        this.endTime = t.getEndTime().toString();
        this.teacherName = t.getTeacher().getName();
        this.division = t.getDivision();
        this.lectureId = lectureId;
        this.status = status;
    }

    // Getters
    public Long getTimetableId() {
        return timetableId;
    }

    public String getSubject() {
        return subject;
    }

    public String getDay() {
        return day;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getDivision() {
        return division;
    }

    public Long getLectureId() {
        return lectureId;
    }

    public String getStatus() {
        return status;
    }
}
