package com.example.mononlinecourses.repository;

import com.example.mononlinecourses.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EnrollmentDao extends JpaRepository<Enrollment, UUID> {
}
