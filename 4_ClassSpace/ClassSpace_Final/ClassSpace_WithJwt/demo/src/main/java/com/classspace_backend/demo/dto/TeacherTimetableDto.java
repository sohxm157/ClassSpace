package com.classspace_backend.demo.dto;

import java.time.LocalTime;

import com.classspace_backend.demo.entity.Timetable.Day;

public class TeacherTimetableDto {

    private Long timetableId;
    private Day day;
    private LocalTime startTime;
    private LocalTime endTime;
    private String subject;
    private String className;
    
    
    public TeacherTimetableDto() {
    	
    }

    public TeacherTimetableDto(
            Long timetableId,
            Day day,
            LocalTime startTime,
            LocalTime endTime,
            String subject,
            String className) {
        this.timetableId = timetableId;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subject = subject;
        this.className = className;
    }

	public Long getTimetableId() {
		return timetableId;
	}

	public void setTimetableId(Long timetableId) {
		this.timetableId = timetableId;
	}

	public Day getDay() {
		return day;
	}

	public void setDay(Day day) {
		this.day = day;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

    

}
