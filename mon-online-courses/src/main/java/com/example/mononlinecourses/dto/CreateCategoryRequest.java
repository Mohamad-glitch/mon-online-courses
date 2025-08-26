package com.example.mononlinecourses.dto;

import jakarta.validation.constraints.NotNull;

public record CreateCategoryRequest (

        @NotNull(message = "Name is required")
        String name
){
}
