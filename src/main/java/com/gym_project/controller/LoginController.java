package com.gym_project.controller;

import com.gym_project.constants.RoutConstants;
import com.gym_project.security.AuthContext;
import com.gym_project.security.AuthService;
import com.gym_project.security.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.AUTH)
@Tag(name = "Authentication")
public class LoginController {

    private static final String SESSION_USERNAME = "AUTH_USERNAME";
    private static final String SESSION_ROLE     = "AUTH_ROLE";

    private final LoginService loginService;
    private final AuthService authService;

    public LoginController(LoginService loginService, AuthService authService) {
        this.loginService = loginService;
        this.authService = authService;
    }

    @GetMapping("/login")
    @Operation(summary = "Login")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    public ResponseEntity<Void> login(
            @Parameter(required = true) @RequestParam String username,
            @Parameter(required = true) @RequestParam String password,
            HttpServletRequest request
    ) {
        loginService.login(username, password);

        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_USERNAME, AuthContext.getUsername());
        session.setAttribute(SESSION_ROLE,     AuthContext.getRole());

        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid username or old password")
    })
    public ResponseEntity<Void> changePassword(
            @Parameter(required = true) @RequestParam String username,
            @Parameter(required = true) @RequestParam String oldPassword,
            @Parameter(required = true) @RequestParam String newPassword
    ) {
        loginService.changePassword(username, oldPassword, newPassword);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logout successful")
    })
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        authService.logout();
        return ResponseEntity.ok().build();
    }
}