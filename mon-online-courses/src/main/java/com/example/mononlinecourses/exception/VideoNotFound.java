package com.example.mononlinecourses.exception;

public class VideoNotFound extends RuntimeException {
    public VideoNotFound(String message) {
        super(message);
    }
}
