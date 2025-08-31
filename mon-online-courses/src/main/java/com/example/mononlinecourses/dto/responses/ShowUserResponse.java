package com.example.mononlinecourses.dto.responses;

public record ShowUserResponse(
        String fullName,
        String  email,
        String bio
) {
}
