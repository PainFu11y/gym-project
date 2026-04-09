package com.gym_project.controller;

import com.gym_project.constants.RoutConstants;
import com.gym_project.security.GymUserDetails;
import com.gym_project.security.JwtService;
import com.gym_project.security.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.AUTH)
@Tag(name = "Authentication")
@RequiredArgsConstructor
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtService            jwtService;
    private final LoginService          loginService;

    @GetMapping("/login")
    @Operation(summary = "Login")
    public ResponseEntity<Map<String, String>> login(
            @RequestParam String username,
            @RequestParam String password
    ) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            GymUserDetails userDetails = (GymUserDetails) auth.getPrincipal();
            String token = jwtService.generateToken(userDetails);
            loginService.recordLoginSuccess();
            return ResponseEntity.ok(Map.of("token", token));

        } catch (LockedException ex) {
            return ResponseEntity.status(HttpStatus.LOCKED)
                    .body(Map.of("error", "Account locked. Try again in 5 minutes."));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        }
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
            @Parameter(required = true) @RequestParam String newPassword
    ) {
        loginService.changePassword(username, oldPassword, newPassword);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Clears the security context for the current user.")
    @ApiResponse(responseCode = "200", description = "Logout successful")
    public ResponseEntity<Void> logout() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        SecurityContextHolder.clearContext();
        log.info("User '{}' logged out", username);
        return ResponseEntity.ok().build();
    }
}