package com.example.mononlinecourses.dto.Requests;

import jakarta.validation.constraints.NotEmpty;

import java.util.UUID;

public record UpdateSection(
        UUID sectionId,

        @NotEmpty(message = "section title should not be empty")
        String sectionTitle
) {
}
