package com.classspace_backend.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.classspace_backend.demo.entity.Announcement;

@Repository
public interface AnnouncementRepository
        extends JpaRepository<Announcement, Long> {

    List<Announcement> findByClassEntity_ClassId(Long classId);

    List<Announcement> findByClassEntity_ClassIdAndDivisionOrderByCreatedAtDesc(Long classId, String division);
}
