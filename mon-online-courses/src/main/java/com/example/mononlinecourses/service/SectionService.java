package com.example.mononlinecourses.service;

import com.example.mononlinecourses.dto.Requests.CreateSectionRequest;
import com.example.mononlinecourses.dto.Requests.UpdateSection;
import com.example.mononlinecourses.dto.responses.ShowCourseSection;
import com.example.mononlinecourses.exception.NotAuthorized;
import com.example.mononlinecourses.exception.SectionHasPostionAlready;
import com.example.mononlinecourses.exception.SectionNotFound;
import com.example.mononlinecourses.mapper.SectionMapper;
import com.example.mononlinecourses.model.Course;
import com.example.mononlinecourses.model.Section;
import com.example.mononlinecourses.repository.SectionDao;
import com.example.mononlinecourses.utils.JwtUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class SectionService {

    private final CourseService courseService;
    private final SectionDao sectionDao;
    private final UserService userService;
    private JwtUtils jwtUtils;

    @Autowired
    public SectionService(CourseService courseService, SectionDao sectionDao, UserService userService) {
        this.courseService = courseService;
        this.sectionDao = sectionDao;
        this.userService = userService;
    }

    @Autowired
    public void setJwtUtils(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    public void isSectionOwnedByInstructor(String token, UUID sectionId) {
        token = token.substring(7);
        if (!sectionDao.isSectionOwnedByInstructor(userService.getInstructorID(jwtUtils.extractEmail(token))
                , sectionId))
            throw new NotAuthorized("you are not authorized to change the section");
    }

    public void checkPositionExists(long sectionPosition, UUID courseId) {
        if (sectionDao.existsByPositionAndCourse(sectionPosition, courseService.getCourseById(courseId)))
            throw new SectionHasPostionAlready("section with this position exists already");

    }

    @Transactional
    public void createSection(CreateSectionRequest createSectionRequest, UUID courseId) {

        Section section = SectionMapper.
                fromCreateSectionRequestToSection(createSectionRequest);

        section.setCourse(courseService.getCourseById(courseId));

        section.setPosition(sectionDao.findMaxPositionByCourseId(courseId) + 1);

        sectionDao.save(section);
    }


    public List<ShowCourseSection> getAllSections(UUID courseId) {
        Course course = courseService.getCourseById(courseId);

        return course.getSections().stream()
                .map(SectionMapper::fromSectionToShowCourseSection).toList();
    }

    public void updateSection(UpdateSection updateSection, String token, UUID sectionId) {
        isSectionOwnedByInstructor(token, sectionId);

        Section updated = getSectionById(sectionId);
        updated.setTitle(updateSection.sectionTitle());
        updated.setUpdatedAt(new Date(System.currentTimeMillis()));
        sectionDao.save(updated);
    }

    public void deleteSectionById(UUID sectionId, String token) {
        isSectionOwnedByInstructor(token, sectionId);

        sectionDao.deleteById(sectionId);
    }


    public Section getSectionById(UUID sectionId) {
        return sectionDao.findById(sectionId).orElseThrow(() -> new SectionNotFound("section was not found"));
    }

}
