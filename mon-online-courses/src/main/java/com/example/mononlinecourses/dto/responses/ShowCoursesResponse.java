package com.example.mononlinecourses.dto.responses;

import java.util.UUID;

public record ShowCoursesResponse(
        UUID courseId,
        String courseTitle,
        String courseDescription,
        double coursePrice,
        long courseDurationInMints,
        double courseRating,
        String courseLanguage
        ) {
}
