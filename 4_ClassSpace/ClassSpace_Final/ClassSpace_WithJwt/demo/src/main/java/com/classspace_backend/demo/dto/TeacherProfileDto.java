package com.classspace_backend.demo.dto;

import java.time.LocalDate;

public class TeacherProfileDto {
    private String name;
    private String email;
    private String subject;
    private String assignedClasses;
    private String assignedDivisions;
    private String phone;
    private LocalDate dob;
    private String address;

    public TeacherProfileDto() {
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAssignedClasses() {
        return assignedClasses;
    }

    public void setAssignedClasses(String assignedClasses) {
        this.assignedClasses = assignedClasses;
    }

    public String getAssignedDivisions() {
        return assignedDivisions;
    }

    public void setAssignedDivisions(String assignedDivisions) {
        this.assignedDivisions = assignedDivisions;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
