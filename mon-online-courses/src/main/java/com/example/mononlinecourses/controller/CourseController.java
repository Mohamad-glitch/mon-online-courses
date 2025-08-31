package com.example.mononlinecourses.controller;

import com.example.mononlinecourses.dto.Requests.CreateCourseRequest;
import com.example.mononlinecourses.dto.Requests.RequestToken;
import com.example.mononlinecourses.dto.responses.PagedResponse;
import com.example.mononlinecourses.dto.responses.ShowCoursesResponse;
import com.example.mononlinecourses.dto.responses.ShowInstructorCourses;
import com.example.mononlinecourses.exception.ImageWasNotSent;
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
@CrossOrigin(origins = "*")
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
    public ResponseEntity<PagedResponse<ShowCoursesResponse>> showAllCourses(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {


        return ResponseEntity.ok().body(courseService.getAllCourses(pageNumber, pageSize));
    }

    //TODO: add the swagger doc to the controller

    @PostMapping("/create-course")
    public ResponseEntity<Void> createCourse(@RequestHeader("Authorization") RequestToken token,
                                             @Valid @ModelAttribute @RequestBody CreateCourseRequest createCourseRequest) {

        isAuthorized(token.token());

        if (createCourseRequest.getCourseImage() == null || createCourseRequest.getCourseImage().isEmpty()) {
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

    @GetMapping("/show-course-image/{id}")
    public ResponseEntity<Resource> getCourseImageByCourseId(@PathVariable UUID id) throws IOException {
        //@RequestHeader("Authorization") String token,
        //isAuthorized(token);

        Resource image = courseService.getCourseImage(id);
        Path path = Paths.get(image.getURI());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path))
                .body(image);
    }


}
