package com.classspace_backend.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.classspace_backend.demo.entity.Note;

@Repository
public interface NotesRepository extends JpaRepository<Note, Long> {

    List<Note> findByLecture_LectureId(Long lectureId);
}

