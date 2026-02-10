package com.classspace_backend.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;
import java.util.HashMap;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<java.util.Map<String, String>> handleNotFound(NotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<java.util.Map<String, String>> handleUnauthorized(UnauthorizedException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<java.util.Map<String, String>> handleBadRequest(BadRequestException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // 🔥 fallback (safety net)
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<java.util.Map<String, String>> handleDataIntegrity(
            org.springframework.dao.DataIntegrityViolationException ex) {
        String msg = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        if (msg.contains("users.phone")) {
            return buildResponse(HttpStatus.BAD_REQUEST, "Phone number already registered");
        }
        if (msg.contains("users.email")) {
            return buildResponse(HttpStatus.BAD_REQUEST, "Email already registered");
        }
        if (msg.contains("password_reset_otp.email")) {
            return buildResponse(HttpStatus.BAD_REQUEST, "OTP already sent. Please wait.");
        }
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Database error: duplicate entry or constraint violation");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<java.util.Map<String, String>> handleAny(Exception ex) {
        ex.printStackTrace(); // server log only
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong.");
    }

    private ResponseEntity<java.util.Map<String, String>> buildResponse(HttpStatus status, String message) {
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
