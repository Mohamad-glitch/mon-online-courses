package com.example.mononlinecourses.dto.Requests;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Setter
@Getter
public class CreateVideoRequest{
    @NotEmpty(message = "video title should not be empty")
    private String videoTitle;
    private MultipartFile video;
}
