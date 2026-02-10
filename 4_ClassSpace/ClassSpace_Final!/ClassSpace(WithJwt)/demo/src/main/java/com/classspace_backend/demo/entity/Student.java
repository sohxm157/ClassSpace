package com.classspace_backend.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

//package com.classspace.backend.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class Student {

	@Id
	@Column(name = "student_id")
	private Long studentId;

	@Column(unique = true)
	private String prn;

	private String branch;
	private String division;

	@OneToOne
	@MapsId // 🔥 THIS IS THE KEY
	@JoinColumn(name = "student_id")
	private User user;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id", insertable = false, updatable = false)
	private IntegrityScore integrityScore;

	// getters & setters

	public Long getStudentId() {
		return studentId;
	}

	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}

	public String getPrn() {
		return prn;
	}

	public void setPrn(String prn) {
		this.prn = prn;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public IntegrityScore getIntegrityScore() {
		return integrityScore;
	}

	public void setIntegrityScore(IntegrityScore integrityScore) {
		this.integrityScore = integrityScore;
	}

}
