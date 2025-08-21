package com.example.mononlinecourses.service;

import com.example.mononlinecourses.dto.CreateCourseRequest;
import com.example.mononlinecourses.exception.InstructorRoleNeeded;
import com.example.mononlinecourses.mapper.Mapper;
import com.example.mononlinecourses.model.Course;
import com.example.mononlinecourses.model.Tag;
import com.example.mononlinecourses.model.User;
import com.example.mononlinecourses.repository.CourseDao;
import com.example.mononlinecourses.repository.TagDao;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class CourseService {
    private final AuthService authService;
    private final UserService userService;
    private final TagDao tagDao;
    private final CourseDao courseDao;


    public CourseService(AuthService authService, UserService userService, TagDao tagDao, CourseDao courseDao) {
        this.authService = authService;
        this.userService = userService;
        this.tagDao = tagDao;
        this.courseDao = courseDao;
    }

    public List<Course> getAllCourses() {

        return courseDao.findAll();
    }

    @Transactional
    public void createCourse(CreateCourseRequest createCourseRequest,
                             MultipartFile courseImage,
                             String instructorEmail
    ) {

        if(!userService.isUserInstructor(instructorEmail)){
            throw new InstructorRoleNeeded("you have to be Instructor to create a course");
        }

        User user = userService.findUserByEmail(instructorEmail).get();


        Tag tag = tagDao.findTagsByName("IT");

        Course createCourse = Mapper.getCourseFromCreateCourseRequest(createCourseRequest);
        createCourse.setDurationMinutes(0);
        createCourse.setCreatedAt(new Date(System.currentTimeMillis()));
        createCourse.setUpdatedAt(new Date(System.currentTimeMillis()));
        createCourse.setInstructor(user);
        createCourse.setTags(Collections.singletonList(tag));
        createCourse.setThumbnailUrl("C:\\Users\\mshlo\\OneDrive\\Desktop\\you question interveiw.jpg");


        courseDao.save(createCourse);




    }
}
