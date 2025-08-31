package com.example.mononlinecourses.repository;

import com.example.mononlinecourses.model.Course;
import com.example.mononlinecourses.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SectionDao extends JpaRepository<Section, UUID> {
    boolean existsByPositionAndCourse(long position, Course course);
}
