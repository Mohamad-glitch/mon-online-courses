package com.example.mononlinecourses.exception;

public class UserExists extends RuntimeException {
    public UserExists(String message) {
        super(message);
    }
}
