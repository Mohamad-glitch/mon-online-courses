package com.example.mononlinecourses.dto.Requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Email(message = "Email is not valid")
        @NotEmpty(message = "Email is required")
        String email,
        @NotEmpty(message = "full name is required")
        String fullName,
        @NotEmpty(message = "password is required")
        @Size(min = 6, message = "password should be at least 6 digits")
        String password
) {
}
