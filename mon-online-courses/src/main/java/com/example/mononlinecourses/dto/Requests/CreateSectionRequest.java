package com.example.mononlinecourses.dto.Requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateSectionRequest(
        @NotEmpty(message = "Section name cant be empty")
        String sectionTitle,

        @NotNull(message = "Position is required")
        @Min(value = 1, message = "sectionPosition should be at least 1 or higher")
        long sectionPosition

) {
}
