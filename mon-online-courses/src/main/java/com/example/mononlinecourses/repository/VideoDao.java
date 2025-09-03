package com.example.mononlinecourses.repository;

import com.example.mononlinecourses.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VideoDao extends JpaRepository<Video, UUID> {

    @Query("SELECT COALESCE(MAX(v.position), 0) FROM Video v WHERE v.section.id = :sectionId")
    long findMaxPositionBySectionId(@Param("sectionId") UUID sectionId);
}
