package com.classspace_backend.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "integrity_score")
public class IntegrityScore {

    @Id
    @Column(name = "student_id")
    private Long studentId;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "student_id")
    private User student;

    @Column(name = "total_lectures", nullable = false)
    private Integer totalLectures = 0;

    @Column(name = "honest_count", nullable = false)
    private Integer honestCount = 0;

    @Column(name = "dishonest_count", nullable = false)
    private Integer dishonestCount = 0;

    @Column(name = "coins", nullable = false)
    private Integer coins = 0;

    @Column(name = "integrity_percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal integrityPercentage = new BigDecimal("100.00");

    // ===== getters & setters =====

    public Long getStudentId() {
        return studentId;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Integer getTotalLectures() {
        return totalLectures;
    }

    public void setTotalLectures(Integer totalLectures) {
        this.totalLectures = totalLectures;
    }

    public Integer getHonestCount() {
        return honestCount;
    }

    public void setHonestCount(Integer honestCount) {
        this.honestCount = honestCount;
    }

    public Integer getDishonestCount() {
        return dishonestCount;
    }

    public void setDishonestCount(Integer dishonestCount) {
        this.dishonestCount = dishonestCount;
    }

    public BigDecimal getIntegrityPercentage() {
        return integrityPercentage;
    }

    public void setIntegrityPercentage(BigDecimal integrityPercentage) {
        this.integrityPercentage = integrityPercentage;
    }

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(Integer coins) {
        this.coins = coins;
    }
}
