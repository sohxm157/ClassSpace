package com.classspace_backend.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.classspace_backend.demo.entity.Student;
import com.classspace_backend.demo.entity.User;

public interface StudentRepository extends JpaRepository<Student, Long> {

	Optional<Student> findById(Long studentId);

	Optional<Student> findByPrn(String prn);

	Optional<Student> findByUser(User user);

	@Query("SELECT s FROM Student s LEFT JOIN FETCH s.integrityScore WHERE s.division = :division AND s.user.isActive = true")
	List<Student> findByDivisionAndUser_IsActiveTrue(@Param("division") String division);

	@Query("SELECT DISTINCT s.division FROM Student s JOIN ClassMember cm ON s.user.userId = cm.user.userId WHERE cm.classEntity.classId = :classId")
	List<String> findDivisionsByClassId(@Param("classId") Long classId);
}
