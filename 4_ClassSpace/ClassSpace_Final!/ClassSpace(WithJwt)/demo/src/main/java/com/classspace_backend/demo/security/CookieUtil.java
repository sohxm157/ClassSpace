package com.classspace_backend.demo.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

public class CookieUtil {

    private CookieUtil() {}

    // ✅ name for your JWT cookie
    public static final String ACCESS_TOKEN_COOKIE = "ACCESS_TOKEN";

    public static String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie c : request.getCookies()) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }

    // ✅ create HttpOnly cookie for JWT
    public static ResponseCookie createAccessCookie(String token) {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(false)          // true in production (https)
                .path("/")
                .sameSite("Lax")        // use "None" + secure(true) if frontend is on different domain with https
                .maxAge(60 * 60)        // 1 hour (match your JWT expiry)
                .build();
    }

    // ✅ clear cookie on logout or forced logout
    public static ResponseCookie clearAccessCookie() {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)              // expire now
                .build();
    }
}


