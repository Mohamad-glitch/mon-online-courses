package com.example.mononlinecourses.dto.Requests;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CreateCourseRequest {

    @NotNull(message = "Course Name is required")
    private String courseName;

    @NotNull(message = "Course Description is required")
    private String courseDescription;

    @NotNull(message = "Course Price is required")
    private double coursePrice;

    @NotNull(message = "Course Language is required")
    private String courseLanguage;

    private List<String> tags;

    private List<String> category;

    private MultipartFile courseImage;

}
