package com.classspace_backend.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.classspace_backend.demo.dto.ChangePasswordDto;
import com.classspace_backend.demo.dto.LoginRequestDto;
import com.classspace_backend.demo.dto.RegisterSendOtpDto;
import com.classspace_backend.demo.dto.RegisterVerifyOtpDto;
import com.classspace_backend.demo.dto.SendOtpDto;
import com.classspace_backend.demo.dto.VerifyOtpDto;
import com.classspace_backend.demo.entity.User;
import com.classspace_backend.demo.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
@CrossOrigin(
    origins = "http://localhost:5173",
    allowCredentials = "true"
)
public class AuthController {

    @Autowired
    private AuthService authService;
    
   
    // ✅ REGISTER OTP
    @PostMapping("/register/send-otp")
    public ResponseEntity<?> sendRegisterOtp(@RequestBody RegisterSendOtpDto dto) {
        return authService.sendRegisterOtp(dto);
    }

    // ✅ REGISTER VERIFY OTP (creates account, may set cookie OR force password change)
    @PostMapping("/register/verify-otp")
    public ResponseEntity<?> verifyRegisterOtp(@RequestBody RegisterVerifyOtpDto dto) {
        // No session needed anymore
        return authService.verifyRegisterOtp(dto);
    }

    // ✅ CHANGE PASSWORD
    // If user is logged in -> change based on JWT user
    // If forgot-password flow -> change based on email + verified otp (your current logic)
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto dto) {
        return authService.changePassword(dto);
    }

    // ✅ FORGOT PASSWORD
    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody SendOtpDto dto) {
        return authService.sendOtp(dto);
    }

    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpDto dto) {
        return authService.verifyOtp(dto);
    }

    // ✅ LOGIN (sets HttpOnly JWT cookie)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto dto) {
        return authService.login(dto);
    }

    // ✅ LOGOUT (clears cookie)
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return authService.logout();
    }

    // ✅ ME (reads user from SecurityContext populated by JwtCookieFilter)
    @GetMapping("/me")
    public ResponseEntity<?> me() {
        return authService.getCurrentUser();
    }
    
   

}
