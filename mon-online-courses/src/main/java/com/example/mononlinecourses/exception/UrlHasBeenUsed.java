package com.example.mononlinecourses.exception;

public class UrlHasBeenUsed extends RuntimeException {
    public UrlHasBeenUsed(String message) {
        super(message);
    }
}
