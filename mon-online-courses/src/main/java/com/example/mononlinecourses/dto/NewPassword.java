package com.example.mononlinecourses.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record NewPassword(
        @Size(min = 6, message = "Password should be at least 6 digits")
        @NotEmpty(message = "Password is required")
        String password
) {
}
