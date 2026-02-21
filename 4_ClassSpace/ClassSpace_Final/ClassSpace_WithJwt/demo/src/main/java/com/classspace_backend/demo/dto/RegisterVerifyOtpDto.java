package com.classspace_backend.demo.dto;

public class RegisterVerifyOtpDto {

    private String name;
    private String email;
    private String dob;
    private String institute;
    private String prn;
    private String phone;
    private String otp;

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDob() { return dob; }
    public String getInstitute() { return institute; }
    public String getPhone() { return phone; }
    public String getOtp() { return otp; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setDob(String dob) { this.dob = dob; }
    public void setInstitute(String institute) { this.institute = institute; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setOtp(String otp) { this.otp = otp; }
	public String getPrn() {
		return prn;
	}
	public void setPrn(String prn) {
		this.prn = prn;
	}
}
