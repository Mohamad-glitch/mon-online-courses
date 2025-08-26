package com.example.mononlinecourses.exception;

public class CategoryNameCantBeEmpty extends RuntimeException {
    public CategoryNameCantBeEmpty(String message) {
        super(message);
    }
}
