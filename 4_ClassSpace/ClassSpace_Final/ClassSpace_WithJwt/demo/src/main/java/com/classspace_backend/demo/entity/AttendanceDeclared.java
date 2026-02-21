package com.classspace_backend.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "attendance_declared",
    uniqueConstraints = @UniqueConstraint(columnNames = {"lecture_id", "student_id"})
)
public class AttendanceDeclared {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Enumerated(EnumType.STRING)
    @Column(name = "declared_status", nullable = false)
    private DeclaredStatus declaredStatus;

    @Column(name = "declared_at", nullable = false)
    private LocalDateTime declaredAt;

    @PrePersist
    protected void onCreate() {
        declaredAt = LocalDateTime.now();
    }

    // ===== getters & setters =====

    public Long getId() {
        return id;
    }

    public Lecture getLecture() {
        return lecture;
    }

    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public DeclaredStatus getDeclaredStatus() {
        return declaredStatus;
    }

    public void setDeclaredStatus(DeclaredStatus declaredStatus) {
        this.declaredStatus = declaredStatus;
    }

    public LocalDateTime getDeclaredAt() {
        return declaredAt;
    }
}
