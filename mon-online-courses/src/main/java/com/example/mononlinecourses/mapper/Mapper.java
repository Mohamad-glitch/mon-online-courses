package com.example.mononlinecourses.mapper;

import com.example.mononlinecourses.dto.CreateCourseRequest;
import com.example.mononlinecourses.dto.RegisterRequest;
import com.example.mononlinecourses.dto.ShowInstructorCourses;
import com.example.mononlinecourses.dto.ShowUserResponse;
import com.example.mononlinecourses.model.Course;
import com.example.mononlinecourses.model.User;

public class Mapper {

    public static User getUserFromDTO(RegisterRequest registerRequest) {
        User user = new User();
        user.setEmail(registerRequest.email());
        user.setPassword(registerRequest.password());
        user.setFullName(registerRequest.fullName());
        return user;
    }

    public static ShowUserResponse getShowUserDTOFromUser(User user) {
        return new ShowUserResponse(
                user.getFullName(),
                user.getEmail(),
                user.getBio()
        );
    }


    public static Course getCourseFromCreateCourseRequest(CreateCourseRequest createCourseRequest) {
        Course course = new Course();
        course.setTitle(createCourseRequest.getCourseName());
        course.setDescription(createCourseRequest.getCourseDescription());
        course.setPrice(createCourseRequest.getCoursePrice());
        course.setLanguage(createCourseRequest.getCourseLanguage());

        return course;
    }

    public static ShowInstructorCourses showInstructorCoursesFromCourse(Course course) {
        return new ShowInstructorCourses(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getPrice(),
                course.getDurationMinutes(),
                course.getLanguage()
        );
    }

}
