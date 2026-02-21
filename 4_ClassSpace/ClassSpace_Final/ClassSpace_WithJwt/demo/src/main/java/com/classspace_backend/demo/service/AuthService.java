package com.classspace_backend.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.classspace_backend.demo.dto.ChangePasswordDto;
import com.classspace_backend.demo.dto.LoginRequestDto;
import com.classspace_backend.demo.dto.RegisterSendOtpDto;
import com.classspace_backend.demo.dto.RegisterVerifyOtpDto;
import com.classspace_backend.demo.dto.SendOtpDto;
import com.classspace_backend.demo.dto.VerifyOtpDto;
import com.classspace_backend.demo.entity.PasswordResetOtp;
import com.classspace_backend.demo.entity.Student;
import com.classspace_backend.demo.entity.User;
import com.classspace_backend.demo.exception.BadRequestException;
import com.classspace_backend.demo.exception.NotFoundException;
import com.classspace_backend.demo.exception.UnauthorizedException;
import com.classspace_backend.demo.repository.ClassMemberRepository;
import com.classspace_backend.demo.repository.PasswordResetOtpRepository;
import com.classspace_backend.demo.repository.RoleRepository;
import com.classspace_backend.demo.repository.StudentRepository;
import com.classspace_backend.demo.repository.UserRepository;
import com.classspace_backend.demo.security.CookieUtil;
import com.classspace_backend.demo.security.JwtUtil;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final ClassMemberRepository classMemberRepository;
    private final PasswordResetOtpRepository otpRepository;

    private final JwtUtil jwtUtil;

    public AuthService(
            UserRepository userRepository,
            StudentRepository studentRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            RoleRepository roleRepository,
            ClassMemberRepository classMemberRepository,
            PasswordResetOtpRepository otpRepository,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.roleRepository = roleRepository;
        this.classMemberRepository = classMemberRepository;
        this.otpRepository = otpRepository;
        this.jwtUtil = jwtUtil;

    }

    // =========================================
    // LOGIN -> issues JWT in HttpOnly cookie
    // =========================================
    public ResponseEntity<?> login(LoginRequestDto dto) {

        String requestedRole = dto.getRole() == null ? "" : dto.getRole().trim().toUpperCase();
        String username = dto.getUsername() == null ? "" : dto.getUsername().trim();
        System.out.println(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("1234"));

        System.out.println("requestedRole=" + requestedRole);
        System.out.println("username=" + username);

        if (requestedRole.isBlank()) {
            throw new BadRequestException("role is required");
        }

        if (username.isBlank() || dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new BadRequestException("username and password are required");
        }

        User user;

        // =========================
        // 1) FETCH USER (based on role)
        // =========================
        if ("STUDENT".equals(requestedRole)) {
            // Student logs in using PRN
            Student student = studentRepository.findByPrn(username)
                    .orElseThrow(() -> new UnauthorizedException("Invalid PRN or password"));

            user = student.getUser();
            if (user == null) {
                throw new UnauthorizedException("Invalid PRN or password");
            }

        } else if ("TEACHER".equals(requestedRole) || "COORDINATOR".equals(requestedRole)) {
            // Teacher/Coordinator logs in using Email
            user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        } else {
            throw new BadRequestException("role must be STUDENT, TEACHER, or COORDINATOR");
        }

        // =========================
        // 2) PASSWORD CHECK
        // =========================
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        // =========================
        // 3) ROLE CHECK (strict match)
        // =========================
        String actualRole = (user.getRole() != null && user.getRole().getRoleName() != null)
                ? user.getRole().getRoleName().trim().toUpperCase()
                : null;

        if (actualRole == null) {
            throw new UnauthorizedException("Role not assigned to user");
        }

        if (!actualRole.equals(requestedRole)) {
            throw new UnauthorizedException("Access denied");
        }

        // =========================
        // 4) FORCE PASSWORD CHANGE LOGIC
        // =========================
        // Rule: If encoded password == username (PRN/email) => force change
        if (passwordEncoder.matches(username, user.getPassword())) {
            String token = jwtUtil.generateAccessToken(user.getUserId(), user.getEmail(), actualRole);
            return ResponseEntity.status(428)
                    .header(HttpHeaders.SET_COOKIE, CookieUtil.createAccessCookie(token).toString())
                    .body("FORCE_PASSWORD_CHANGE");
        }

        // =========================
        // 5) ISSUE JWT COOKIE
        // =========================
        String token = jwtUtil.generateAccessToken(user.getUserId(), user.getEmail(), actualRole);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, CookieUtil.createAccessCookie(token).toString())
                .body(actualRole + "_LOGIN_SUCCESS");
    }

    // =========================================
    // LOGOUT -> clears cookie
    // =========================================
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, CookieUtil.clearAccessCookie().toString())
                .body("Logged out");
    }

    // =========================================
    // /auth/me -> uses SecurityContext (JWT filter)
    // =========================================
    public ResponseEntity<?> getCurrentUser() {

        User user = getCurrentUserEntity(); // from SecurityContext

        String className = classMemberRepository
                .findTopByUserAndStatus(user, "APPROVED")
                .map(cm -> cm.getClassEntity().getClassName())
                .orElse(null);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getUserId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().getRoleName());
        response.put("className", className);

        return ResponseEntity.ok(response);
    }

    // =========================================
    // INTERNAL: Current user entity (JWT auth)
    // =========================================
    public User getCurrentUserEntity() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UnauthorizedException("User not logged in");
        }

        String email = auth.getName(); // must be set by JWT filter as email

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    // =========================================
    // CHANGE PASSWORD
    // 1) Logged-in user -> change directly
    // 2) Forgot password -> OTP verified flow
    // =========================================
    @Transactional
    public ResponseEntity<?> changePassword(ChangePasswordDto dto) {

        User user;

        // CASE 1: logged in user (JWT cookie)
        // If token exists, SecurityContext will have it.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean loggedIn = auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal());

        if (loggedIn) {
            user = getCurrentUserEntity();
        }
        // CASE 2: forgot password (OTP verified)
        else {
            String identifier = dto.getEmail();
            if (identifier == null || identifier.isBlank()) {
                throw new BadRequestException("Email/PRN is required for password reset");
            }

            // Try finding user by Email or PRN
            user = userRepository.findByEmail(identifier).orElse(null);
            if (user == null) {
                user = studentRepository.findByPrn(identifier)
                        .map(Student::getUser)
                        .orElse(null);
            }

            if (user == null) {
                throw new NotFoundException("User not found");
            }

            final User finalUser = user;
            // OTP verification (using user's email)
            PasswordResetOtp otpEntity = otpRepository
                    .findTopByEmailOrderByCreatedAtDesc(finalUser.getEmail())
                    .orElseThrow(() -> new BadRequestException("OTP not found for " + finalUser.getEmail()));

            if (!otpEntity.isVerified()) {
                throw new UnauthorizedException("OTP not verified");
            }

            otpRepository.delete(otpEntity); // cleanup
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        // Optional: after password change, you may want to force logout:
        // Clear cookie so user must login again.
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, CookieUtil.clearAccessCookie().toString())
                .body("PASSWORD_CHANGED");
    }

    // =========================================
    // SEND OTP (forgot password)
    // =========================================
    @Transactional
    public ResponseEntity<?> sendOtp(SendOtpDto dto) {

        userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new NotFoundException("Email not registered"));

        // Option A: Update existing if found
        PasswordResetOtp entity = otpRepository.findTopByEmailOrderByCreatedAtDesc(dto.getEmail())
                .orElse(new PasswordResetOtp());

        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);

        entity.setEmail(dto.getEmail());
        entity.setOtp(otp);
        entity.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        entity.setVerified(false);
        entity.setCreatedAt(LocalDateTime.now());

        otpRepository.save(entity);

        emailService.sendForgotPasswordOtp(dto.getEmail(), otp);

        return ResponseEntity.ok("OTP_SENT");
    }

    // =========================================
    // VERIFY OTP
    // =========================================
    public ResponseEntity<?> verifyOtp(VerifyOtpDto dto) {

        PasswordResetOtp otpEntity = otpRepository
                .findTopByEmailOrderByCreatedAtDesc(dto.getEmail())
                .orElseThrow(() -> new BadRequestException("OTP not found"));

        if (otpEntity.isVerified()) {
            throw new BadRequestException("OTP already used");
        }

        if (otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP expired");
        }

        if (!otpEntity.getOtp().equals(dto.getOtp())) {
            throw new UnauthorizedException("Invalid OTP");
        }

        otpEntity.setVerified(true);
        otpRepository.save(otpEntity);

        return ResponseEntity.ok("OTP_VERIFIED");
    }

    // =========================================
    // REGISTER SEND OTP
    // =========================================
    @Transactional
    public ResponseEntity<?> sendRegisterOtp(RegisterSendOtpDto dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BadRequestException("Email already registered");
        }

        if (studentRepository.findByPrn(dto.getPrn()).isPresent()) {
            throw new BadRequestException("PRN already registered");
        }

        if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
            if (userRepository.existsByPhone(dto.getPhone())) {
                throw new BadRequestException("Phone number already registered");
            }
        }

        // Option A: Update existing if found
        PasswordResetOtp entity = otpRepository.findTopByEmailOrderByCreatedAtDesc(dto.getEmail())
                .orElse(new PasswordResetOtp());

        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);

        entity.setEmail(dto.getEmail());
        entity.setOtp(otp);
        entity.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        entity.setVerified(false);
        entity.setCreatedAt(LocalDateTime.now());

        otpRepository.save(entity);

        emailService.sendRegisterOtp(dto.getEmail(), otp, dto.getPrn());

        return ResponseEntity.ok("REGISTER_OTP_SENT");
    }

    // =========================================
    // REGISTER VERIFY OTP -> create user + student
    // returns 428 FORCE_PASSWORD_CHANGE (same behavior)
    // =========================================
    @Transactional
    public ResponseEntity<?> verifyRegisterOtp(RegisterVerifyOtpDto dto) {

        PasswordResetOtp otpEntity = otpRepository
                .findTopByEmailOrderByCreatedAtDesc(dto.getEmail())
                .orElseThrow(() -> new BadRequestException("OTP not found"));

        if (studentRepository.findByPrn(dto.getPrn()).isPresent()) {
            throw new BadRequestException("PRN already registered");
        }

        if (otpEntity.isVerified()) {
            throw new BadRequestException("OTP already used");
        }

        if (otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP expired");
        }

        if (!otpEntity.getOtp().equals(dto.getOtp())) {
            throw new BadRequestException("Invalid OTP");
        }

        otpEntity.setVerified(true);
        otpRepository.save(otpEntity);

        // CREATE USER with temp password=PRN (Issue 1)
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPrn()));
        user.setRole(
                roleRepository.findByRoleName("STUDENT")
                        .orElseThrow(() -> new NotFoundException("Role not found")));
        user.setDob(LocalDate.parse(dto.getDob()));
        user.setPhone(dto.getPhone());

        userRepository.save(user);
        userRepository.flush();

        // CREATE STUDENT
        Student student = new Student();
        student.setUser(user);
        student.setPrn(dto.getPrn());
        student.setBranch(null);
        student.setDivision(null);

        studentRepository.save(student);

        // Same behavior: force password change
        String token = jwtUtil.generateAccessToken(user.getUserId(), user.getEmail(), "STUDENT");
        return ResponseEntity.status(428)
                .header(HttpHeaders.SET_COOKIE, CookieUtil.createAccessCookie(token).toString())
                .body("FORCE_PASSWORD_CHANGE");
    }

    // =========================================
    // Optional: helper if you still want it
    // =========================================
    public String createToken(User user) {
        // Use your JwtUtil's generateAccessToken, not some jwtUtil.generate(...)
        return jwtUtil.generateAccessToken(
                user.getUserId(),
                user.getEmail(),
                user.getRole().getRoleName());
    }
}
