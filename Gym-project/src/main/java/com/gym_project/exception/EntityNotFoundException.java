package com.gym_project.exception;

public class EntityNotFoundException extends AppException {

    public EntityNotFoundException(String message) {
        super(message, 404);
    }
}