package com.example.mononlinecourses.dto.Requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record ResetPasswordRequest(
        @Email(message = "Email is not valid")
        @NotEmpty(message = "Email is required")
        String email
) {
}
