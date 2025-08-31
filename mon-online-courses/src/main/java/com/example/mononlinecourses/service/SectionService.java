package com.example.mononlinecourses.service;

import com.example.mononlinecourses.dto.Requests.CreateSectionRequest;
import com.example.mononlinecourses.dto.responses.ShowCourseSection;
import com.example.mononlinecourses.exception.SectionHasPostionAlready;
import com.example.mononlinecourses.mapper.SectionMapper;
import com.example.mononlinecourses.model.Course;
import com.example.mononlinecourses.model.Section;
import com.example.mononlinecourses.repository.SectionDao;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SectionService {

    private final CourseService courseService;
    private final SectionDao sectionDao;

    public SectionService(CourseService courseService, SectionDao sectionDao) {
        this.courseService = courseService;
        this.sectionDao = sectionDao;
    }


    public void checkPositionExists(long sectionPosition, UUID courseId) {
        if(sectionDao.existsByPositionAndCourse(sectionPosition, courseService.getCourseById(courseId)))
            throw new SectionHasPostionAlready("section with this position exists already");

    }

    @Transactional
    public void createSection(CreateSectionRequest createSectionRequest, UUID courseId) {

        Section section = SectionMapper.
                fromCreateSectionRequestToSection(createSectionRequest);

        section.setCourse(courseService.getCourseById(courseId));

        checkPositionExists(createSectionRequest.sectionPosition(), courseId);

        sectionDao.save(section);
    }


    public List<ShowCourseSection> getAllSections(UUID courseId) {
        Course course = courseService.getCourseById(courseId);

        return course.getSections().stream()
                .map(SectionMapper::fromSectionToShowCourseSection).toList();
    }
}
