package com.example.mononlinecourses.controller;

import com.example.mononlinecourses.dto.CreateCourseRequest;
import com.example.mononlinecourses.dto.RequestToken;
import com.example.mononlinecourses.dto.ShowInstructorCourses;
import com.example.mononlinecourses.exception.ImageWasNotSent;
import com.example.mononlinecourses.model.Course;
import com.example.mononlinecourses.service.AuthService;
import com.example.mononlinecourses.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

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

    //TODO: add the swagger doc to the controller

    @PostMapping("/create-course")
    public ResponseEntity<Void> createCourse(@RequestHeader("Authorization") RequestToken token,
                                             @Valid @ModelAttribute @RequestBody CreateCourseRequest createCourseRequest) {

        isAuthorized(token.token());

        if(createCourseRequest.getCourseImage() == null || createCourseRequest.getCourseImage().isEmpty()) {
            throw new ImageWasNotSent("there was no image was provided");
        }

        courseService.createCourse(createCourseRequest,
                createCourseRequest.getCourseImage(),
                authService.extractUserEmailFromToken(token.token()));

        return ResponseEntity.ok().build();
    }

    @GetMapping("/show-all-my-courses")
    public ResponseEntity<List<ShowInstructorCourses>> showAllMyCourses(@RequestHeader("Authorization") String token) {

        isAuthorized(token);


        return ResponseEntity.ok().body(courseService.getAllInstructorCourses(authService.extractUserEmailFromToken(token)));
    }

    @GetMapping("/show-all-my-courses/{id}")
    public ResponseEntity<Resource> getCourseImageByCourseId(@RequestHeader("Authorization") String token, @PathVariable UUID id) throws IOException {
        isAuthorized(token);

        Resource image = courseService.getCourseImage(id);
        Path path = Paths.get(image.getURI());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path))
                .body(image);
    }

}
