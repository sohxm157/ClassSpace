package com.classspace_backend.demo.dto;

import java.time.LocalDate;

public class StudentProfileDto {

	private String name;
	private String email;
	private String phone;
	private LocalDate dob;
	private String address;

	private String prn;
	private String branch;
	private String division;
	private String className;

	// integrity
	private Integer coins;
	private java.math.BigDecimal integrityPercentage;

	public StudentProfileDto() {
		// TODO Auto-generated constructor stub
	}

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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Integer getCoins() {
		return coins;
	}

	public void setCoins(Integer coins) {
		this.coins = coins;
	}

	public java.math.BigDecimal getIntegrityPercentage() {
		return integrityPercentage;
	}

	public void setIntegrityPercentage(java.math.BigDecimal integrityPercentage) {
		this.integrityPercentage = integrityPercentage;
	}
}
