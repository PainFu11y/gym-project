package com.gym_project.exception;

public class UnauthorizedException extends AppException {
    public UnauthorizedException(String message) { super(message, 401); }
}