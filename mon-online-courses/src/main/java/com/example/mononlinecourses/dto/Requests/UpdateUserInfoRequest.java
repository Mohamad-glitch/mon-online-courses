package com.example.mononlinecourses.dto.Requests;

import jakarta.validation.constraints.NotEmpty;

public record UpdateUserInfoRequest(
        @NotEmpty(message = "Name should not be empty")
        String fullName,
        String bio
) {
}
