package com.example.mononlinecourses.dto.Requests;

import jakarta.validation.constraints.NotEmpty;

public record UpdateSection(
        @NotEmpty(message = "section title should not be empty")
        String sectionTitle
) {
}
