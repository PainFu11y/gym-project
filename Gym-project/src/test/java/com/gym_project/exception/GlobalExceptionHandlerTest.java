package com.gym_project.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_shouldReturn404() {
        EntityNotFoundException ex = new EntityNotFoundException("Trainee 'x' not found");

        ResponseEntity<ErrorResponseDto> response = handler.handleNotFound(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Trainee 'x' not found");
        assertThat(response.getBody().getStatus()).isEqualTo(404);
    }

    @Test
    void handleInvalidCredentials_shouldReturn401() {
        InvalidCredentialsException ex = new InvalidCredentialsException();

        ResponseEntity<ErrorResponseDto> response = handler.handleInvalidCredentials(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid username or password");
    }

    @Test
    void handleUnauthorized_shouldReturn401WithStandardMessage() {
        UnauthorizedException ex = new UnauthorizedException("Not authenticated");

        ResponseEntity<ErrorResponseDto> response = handler.handleNotAuthenticated(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(response.getBody().getMessage()).contains("Authentication required");
    }

    @Test
    void handleForbiddenOperation_shouldReturnExceptionStatus() {
        ForbiddenOperationException ex = new ForbiddenOperationException("Only trainer can delete");

        ResponseEntity<ErrorResponseDto> response = handler.handleForbiddenOperation(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(ex.getStatus());
        assertThat(response.getBody().getMessage()).isEqualTo("Only trainer can delete");
    }

    @Test
    void handleAccessDenied_shouldReturn403() {
        AccessDeniedException ex = new AccessDeniedException("Forbidden");

        ResponseEntity<ErrorResponseDto> response = handler.handleAccessDenied(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(403);
        assertThat(response.getBody().getMessage()).contains("Access denied");
    }

    @Test
    void handleBadCredentials_shouldReturn401() {
        BadCredentialsException ex = new BadCredentialsException("Wrong credentials");

        ResponseEntity<ErrorResponseDto> response = handler.handleBadCredentials(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid username or password");
    }

    @Test
    void handleLocked_shouldReturn423() {
        LockedException ex = new LockedException("Locked");

        ResponseEntity<ErrorResponseDto> response = handler.handleLocked(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(423);
        assertThat(response.getBody().getMessage()).contains("Account locked");
    }

    @Test
    void handleDataIntegrity_shouldReturn409() {
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("Unique constraint violated");

        ResponseEntity<ErrorResponseDto> response = handler.handleDataIntegrity(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody().getMessage()).contains("Data conflict");
    }

    @Test
    void handleAppException_shouldReturnCustomStatus() {
        AppException ex = new AppException("Custom error", 422);

        ResponseEntity<ErrorResponseDto> response = handler.handleAppException(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(422);
        assertThat(response.getBody().getMessage()).isEqualTo("Custom error");
    }

    @Test
    void handleGeneric_shouldReturn500() {
        Exception ex = new RuntimeException("Something broke");

        ResponseEntity<ErrorResponseDto> response = handler.handleGeneric(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
    }
}
