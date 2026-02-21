package com.classspace_backend.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "class_members")
public class ClassMember {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "class_id")
	private Classes classEntity;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "role_in_class")
	private String roleInClass;

	@Column(name = "status")
	private String status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Classes getClassEntity() {
		return classEntity;
	}

	public void setClassEntity(Classes classEntity) {
		this.classEntity = classEntity;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getRoleInClass() {
		return roleInClass;
	}

	public void setRoleInClass(String roleInClass) {
		this.roleInClass = roleInClass;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
