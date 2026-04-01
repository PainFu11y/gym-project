package com.gym_project.controller;

import com.gym_project.constants.RoutConstants;
import com.gym_project.security.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.AUTH)
@Tag(name = "Authentication")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;


    @GetMapping("/login")
    @SecurityRequirement(name = "basicAuth")
    @Operation(
            summary = "Login",
            description = "Authenticate using HTTP Basic (Authorization: Basic header). " +
                    "Returns 200 if credentials are valid, 401 otherwise."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<Void> login() {
    return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed — re-authentication required"),
            @ApiResponse(responseCode = "401", description = "Invalid old password or not authenticated")
    })
    public ResponseEntity<Void> changePassword(
            @Parameter(required = true) @RequestParam String username,
            @Parameter(required = true) @RequestParam String oldPassword,
            @Parameter(required = true) @RequestParam String newPassword,
            HttpServletRequest request
    ) {
        loginService.changePassword(username, oldPassword, newPassword);
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

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
        return ResponseEntity.ok().build();
    }
}