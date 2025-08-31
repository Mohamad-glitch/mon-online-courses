package com.example.mononlinecourses.dto.responses;

import java.util.UUID;

public record ShowCourseSection (
        UUID sectionId,
        String sectionTitle,
        long sectionPosition
){
}
