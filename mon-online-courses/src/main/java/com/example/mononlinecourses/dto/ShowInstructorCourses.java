package com.example.mononlinecourses.dto;

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
