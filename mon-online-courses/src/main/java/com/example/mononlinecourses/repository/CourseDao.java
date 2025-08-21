package com.example.mononlinecourses.repository;

import com.example.mononlinecourses.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseDao extends JpaRepository<Course, UUID> {
}
