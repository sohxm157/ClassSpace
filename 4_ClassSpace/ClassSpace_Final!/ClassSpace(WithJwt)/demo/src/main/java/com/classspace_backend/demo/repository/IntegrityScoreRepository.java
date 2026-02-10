package com.classspace_backend.demo.repository;

import com.classspace_backend.demo.entity.IntegrityScore;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IntegrityScoreRepository
        extends JpaRepository<IntegrityScore, Long> {
	
	Optional<IntegrityScore> findByStudent_UserId(Long studentId);
}
