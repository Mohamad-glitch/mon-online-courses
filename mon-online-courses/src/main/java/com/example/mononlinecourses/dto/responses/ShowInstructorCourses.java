package com.example.mononlinecourses.dto.responses;

import java.util.UUID;

public record ShowInstructorCourses(
        UUID id,
        String title,
        String description,
        double price,
        long durationInMints,
        String language
) {
}
