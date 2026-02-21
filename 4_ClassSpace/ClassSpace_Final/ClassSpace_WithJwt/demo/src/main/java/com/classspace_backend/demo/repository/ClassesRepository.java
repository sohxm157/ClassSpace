package com.classspace_backend.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.classspace_backend.demo.entity.Classes;

@Repository
public interface ClassesRepository extends JpaRepository<Classes, Long> {

    Optional<Classes> findByClassLink(String classLink);
}
