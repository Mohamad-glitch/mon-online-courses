package com.example.mononlinecourses.dto;

import jakarta.validation.constraints.NotEmpty;

public record CreateTagRequest(
        @NotEmpty(message = "Tag name is required")
        String tagName
) {
}
