package com.classspace_backend.demo.dto;

import java.time.LocalDate;

public class ResolveLectureRequest {
    private Long timetableId;
    private LocalDate date; // optional, if null -> today

    public Long getTimetableId() { return timetableId; }
    public void setTimetableId(Long timetableId) { this.timetableId = timetableId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}

