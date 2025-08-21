package com.example.mononlinecourses.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCourseRequest {

    @NotNull(message = "Course Name is required")
    private String courseName;

    @NotNull(message = "Course Description is required")
    private String courseDescription;

    @NotNull(message = "Course Price is required")
    private double coursePrice;

    @NotNull(message = "Course Language is required")
    private String courseLanguage;

    private MultipartFile courseImage;

}
