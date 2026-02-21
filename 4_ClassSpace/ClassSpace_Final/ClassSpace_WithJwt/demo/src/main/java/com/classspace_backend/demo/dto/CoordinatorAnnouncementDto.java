package com.classspace_backend.demo.dto;

public class CoordinatorAnnouncementDto {
    private String title;
    private String message;
    private Long classId; // Null if global? The prompt says "Class-wise" or "Division-wise".
    private String division; // Null if class-wise only

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }
}
