package com.example.mononlinecourses.controller;

import com.example.mononlinecourses.dto.Requests.CreateSectionRequest;
import com.example.mononlinecourses.dto.Requests.UpdateSection;
import com.example.mononlinecourses.dto.responses.ShowCourseSection;
import com.example.mononlinecourses.model.Section;
import com.example.mononlinecourses.repository.SectionDao;
import com.example.mononlinecourses.service.AuthService;
import com.example.mononlinecourses.service.SectionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

        try {
            UUID uuid = UUID.fromString(courseId);


            sectionService.createSection(createSectionRequest, uuid);

            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/show-all-sections/{courseId}")
    public ResponseEntity<List<ShowCourseSection>> getAllSectionsFromCourse(@PathVariable String courseId) {
        try {
            UUID uuid = UUID.fromString(courseId);
            List<ShowCourseSection> result = sectionService.getAllSections(uuid);

            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    @PatchMapping("/update-section")
    public ResponseEntity<Void> updateSection(
            @RequestHeader("Authorization")String token,
            @RequestBody UpdateSection updateSection
    ) {
        isAuthorized(token);

        sectionService.updateSection(updateSection, token);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/delete-section/{section}")
    public ResponseEntity<Void> deleteSection(@PathVariable("section") String sectionId) {
        try{

            UUID uuid = UUID.fromString(sectionId);

            sectionService.deleteSectionById(uuid);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

}
