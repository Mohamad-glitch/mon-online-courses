package com.example.mononlinecourses.exception;

public class UserPasswordOrEmailIsNotCorrect extends RuntimeException {
    public UserPasswordOrEmailIsNotCorrect(String message) {
        super(message);
    }
}
