package com.classspace_backend.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "attendance_actual",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"lecture_id", "student_id"})
    }
)
public class AttendanceActual {

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
    @Column(name = "actual_status", nullable = false)
    private ActualStatus actualStatus;

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

    public ActualStatus getActualStatus() {
        return actualStatus;
    }

    public void setActualStatus(ActualStatus actualStatus) {
        this.actualStatus = actualStatus;
    }
}
