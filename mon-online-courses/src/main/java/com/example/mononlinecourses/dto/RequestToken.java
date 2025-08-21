package com.example.mononlinecourses.dto;

import jakarta.validation.constraints.NotEmpty;

public record RequestToken(
        @NotEmpty(message = "Token is required")
        String token
) {
}
