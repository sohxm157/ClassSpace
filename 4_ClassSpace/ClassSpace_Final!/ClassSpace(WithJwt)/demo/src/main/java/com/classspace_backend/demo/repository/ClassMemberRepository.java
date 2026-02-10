package com.classspace_backend.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.classspace_backend.demo.entity.ClassMember;
import com.classspace_backend.demo.entity.User;

@Repository
public interface ClassMemberRepository
        extends JpaRepository<ClassMember, Long> {

	Optional<ClassMember> findTopByUserAndStatus(User user, String status);
;

	Optional<ClassMember> findByUser_UserId(Long userId);
	void deleteByUser_UserId(Long studentId);

}
