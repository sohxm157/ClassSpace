package com.classspace_backend.demo.dto;

public class AddTeacherDto {

    private String name;
    private String email;
    private String subject;
    private String assignedClasses;
    private String assignedDivisions;

    // Optional: phone, address, etc. if required by User entity constraints?
    // The prompt only listed Name, Email, Subject, Assigned class(es), Assigned
    // division(s).
    // But User entity needs password, so we might auto-generate it or ask for it.
    // I'll add password to be safe, or we can set a default.
    private String password;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
