package com.example.mononlinecourses.repository;

import com.example.mononlinecourses.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public interface CourseDao extends JpaRepository<Course, UUID> {
    List<Course> getCoursesByInstructor_Email(String instructorEmail);

    List<Course> id(UUID id);

    Course findCoursesById(UUID id);
}
