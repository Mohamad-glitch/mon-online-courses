package com.example.mononlinecourses.repository;

import com.example.mononlinecourses.model.Course;
import com.example.mononlinecourses.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SectionDao extends JpaRepository<Section, UUID> {
    boolean existsByPositionAndCourse(long position, Course course);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Section s " +
            "WHERE s.id = :sectionId AND s.course.instructor.id = :instructorId")
    boolean isSectionOwnedByInstructor(@Param("instructorId") UUID instructorId,
                                       @Param("sectionId") UUID sectionId);

}
