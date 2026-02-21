package com.classspace_backend.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "teachers")
public class Teacher {

    @Id
    @Column(name = "teacher_id")
    private Long teacherId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "teacher_id")
    private User user;

    @Column(name = "subject")
    private String subject;

    @Column(name = "assigned_classes")
    private String assignedClasses;

    @Column(name = "assigned_divisions")
    private String assignedDivisions;

    // Getters and Setters

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}
