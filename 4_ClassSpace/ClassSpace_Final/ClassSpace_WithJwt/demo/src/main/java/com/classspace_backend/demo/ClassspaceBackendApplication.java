package com.classspace_backend.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ClassspaceBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClassspaceBackendApplication.class, args);
        System.out.println(new BCryptPasswordEncoder().encode("teacher123"));

    }
}
