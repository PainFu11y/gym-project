package com.gym_project.exception;

public class InvalidCredentialsException extends AppException {

    public InvalidCredentialsException() {
        super("Invalid username or password", 401);
    }
}