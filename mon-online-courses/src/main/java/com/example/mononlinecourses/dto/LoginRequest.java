package com.example.mononlinecourses.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record LoginRequest(
        @Email(message = "Email not valid")
        @NotEmpty(message = "Email is required")
        String email,

        @NotEmpty(message = "Password is required")
        String password
) {
}
