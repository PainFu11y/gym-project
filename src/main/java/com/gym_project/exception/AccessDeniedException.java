package com.gym_project.exception;

public class AccessDeniedException extends AppException {
    public AccessDeniedException(String message) { super(message, 403); }
}