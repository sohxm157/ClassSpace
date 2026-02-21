package com.classspace_backend.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.classspace_backend.demo.security.CustomUserDetailsService;
import com.classspace_backend.demo.security.JwtCookieAuthFilter;

@Configuration
@EnableMethodSecurity // enables @PreAuthorize
public class SecurityConfig {

    private final JwtCookieAuthFilter jwtCookieAuthFilter;
    private final CorsConfig corsConfig;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(
            JwtCookieAuthFilter jwtCookieAuthFilter,
            CorsConfig corsConfig,
            CustomUserDetailsService userDetailsService) {
        this.jwtCookieAuthFilter = jwtCookieAuthFilter;
        this.corsConfig = corsConfig;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/coordinator/**").hasRole("COORDINATOR")

                        // 🔥 ATTENDANCE
                        .requestMatchers("/api/attendance/declare").hasRole("STUDENT")
                        .requestMatchers("/api/attendance/actual/**").hasAnyRole("TEACHER", "COORDINATOR")
                        .requestMatchers("/api/attendance/integrity/**").authenticated()

                        // 🔥 FEEDBACK (EXPLICIT — NO ROLE_)
                        .requestMatchers("/api/feedback/submit").hasRole("STUDENT")
                        .requestMatchers("/api/feedback/status/**").hasRole("STUDENT")
                        .requestMatchers("/api/feedback/lecture/**").hasRole("TEACHER")

                        // 🔥 TEACHER
                        .requestMatchers("/api/teacher/**").hasRole("TEACHER")

                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())

                // your JWT cookie filter should run before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtCookieAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Needed for /auth/login to verify password using UserDetailsService
    @Bean
    public AuthenticationProvider authenticationProvider(
            PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}
