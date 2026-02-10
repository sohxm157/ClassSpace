package com.classspace_backend.demo.dto;

public class LoginRequestDto {
	 private String username; 
    private String password;
    private String role; // STUDENT or TEACHER
    

    // getters & setters
	public String getUsername() {
		return username;
	}
	public void setUsername(String email) {
		this.username = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}

}

