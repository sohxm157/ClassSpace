package com.classspace_backend.demo.dto;

import java.util.List;
import java.util.Map;

public class StudentTimetableDto {

    private String className;
    private String division;
    private int weekNumber;

    // MON -> [ {lecture}, {lecture} ]
    private Map<String, List<LectureSlotDto>> timetable;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public int getWeekNumber() {
		return weekNumber;
	}

	public void setWeekNumber(int weekNumber) {
		this.weekNumber = weekNumber;
	}

	public Map<String, List<LectureSlotDto>> getTimetable() {
		return timetable;
	}

	public void setTimetable(Map<String, List<LectureSlotDto>> timetable) {
		this.timetable = timetable;
	}

    // getters & setters
    
}
