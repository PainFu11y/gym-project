package com.gym_project.exception;

public class ForbiddenOperationException extends AppException {
    public ForbiddenOperationException(String message) {
        super(message, 400);
    }
}