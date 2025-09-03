package com.example.mononlinecourses.dto.Requests;

import jakarta.validation.constraints.NotEmpty;

public record CreateSectionRequest(
        @NotEmpty(message = "Section name cant be empty")
        String sectionTitle
) {
}
