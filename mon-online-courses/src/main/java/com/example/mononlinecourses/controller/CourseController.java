package com.example.mononlinecourses.controller;

import com.example.mononlinecourses.dto.CreateCourseRequest;
import com.example.mononlinecourses.dto.RequestToken;
import com.example.mononlinecourses.model.Course;
import com.example.mononlinecourses.service.AuthService;
import com.example.mononlinecourses.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/courses")
@CrossOrigin(origins = "http://localhost:4000")
public class CourseController {

    private final AuthService authService;
    private final CourseService courseService;

    public CourseController(AuthService authService, CourseService courseService) {
        this.authService = authService;
        this.courseService = courseService;
    }

    public void isAuthorized(String token) {
        if (!token.startsWith("Bearer ") || !authService.isValidToken(token.substring(7))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/show-all-courses")
    public ResponseEntity<Void> showAllCourses() {

        List<Course> courses = courseService.getAllCourses();
        courses.forEach(System.out::println);

        return ResponseEntity.ok().build();
    }


    @PostMapping("/create-course")
    public ResponseEntity<Void> createCourse(@RequestHeader("Authorization") RequestToken token,
            @Valid @ModelAttribute @RequestBody CreateCourseRequest createCourseRequest) {

        isAuthorized(token.token());

        courseService.createCourse(createCourseRequest,
                createCourseRequest.getCourseImage(),
                authService.extractUserEmailFromToken(token.token()));

        return ResponseEntity.ok().build();
    }


}
