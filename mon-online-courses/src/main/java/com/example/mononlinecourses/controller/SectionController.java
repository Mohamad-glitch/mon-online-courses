package com.example.mononlinecourses.controller;

import com.example.mononlinecourses.dto.Requests.CreateSectionRequest;
import com.example.mononlinecourses.dto.Requests.UpdateSection;
import com.example.mononlinecourses.dto.responses.ShowCourseSection;
import com.example.mononlinecourses.service.AuthService;
import com.example.mononlinecourses.service.SectionService;
import com.example.mononlinecourses.utils.UUIDValidator;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/section")
public class SectionController {

    private final SectionService sectionService;
    private final AuthService authService;

    public SectionController(SectionService sectionService, AuthService authService) {
        this.sectionService = sectionService;
        this.authService = authService;
    }

    public void isAuthorized(String token) {
        if (!token.startsWith("Bearer ") || !authService.isValidToken(token.substring(7))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
    // TODO: add create and read from the CRUD

    // create a section for the course
    @PostMapping("/create-section/{courseId}")
    public ResponseEntity<Void> createSection(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CreateSectionRequest createSectionRequest,
            @PathVariable String courseId) {

        isAuthorized(token);


        UUID uuid = UUIDValidator.validateUUID(courseId);


        sectionService.createSection(createSectionRequest, uuid);

        return ResponseEntity.status(HttpStatus.CREATED).build();


    }


    @GetMapping("/show-all-sections/{courseId}")
    public ResponseEntity<List<ShowCourseSection>> getAllSectionsFromCourse(@PathVariable String courseId) {

        UUID uuid = UUIDValidator.validateUUID(courseId);
        List<ShowCourseSection> result = sectionService.getAllSections(uuid);

        return ResponseEntity.status(HttpStatus.OK).body(result);

    }


    @PatchMapping("/update-section/{sectionId}")
    public ResponseEntity<Void> updateSection(
            @RequestHeader("Authorization") String token,
            @RequestBody UpdateSection updateSection,
            @PathVariable String sectionId
    ) {
        isAuthorized(token);

        UUID sectionUUID = UUIDValidator.validateUUID(sectionId);
        sectionService.updateSection(updateSection, token, sectionUUID);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/delete-section/{section}")
    public ResponseEntity<Void> deleteSection(
            @RequestHeader("Authorization") String token,
            @PathVariable("section") String sectionId
    ) {

        isAuthorized(token);


        UUID uuid = UUIDValidator.validateUUID(sectionId);

        sectionService.deleteSectionById(uuid, token);
        return ResponseEntity.status(HttpStatus.OK).build();

    }

}
