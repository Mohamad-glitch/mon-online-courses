package com.example.mononlinecourses.repository;

import com.example.mononlinecourses.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VideoDao extends JpaRepository<Video, UUID> {
}
