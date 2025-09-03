package com.example.mononlinecourses.dto.Requests;

import jakarta.validation.constraints.NotEmpty;

public record UpdateVideoRequest(
        @NotEmpty(message = "Video Title cant be empty")
        String videoTitle
) {
}
