package com.gym_project.controller;

import com.gym_project.constants.RoutConstants;
import com.gym_project.security.AuthContext;
import com.gym_project.security.AuthService;
import com.gym_project.security.LoginService;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.AUTH)
@Api(tags = "Authentication")
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
    @ApiOperation(value = "Login")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Login successful"),
            @ApiResponse(code = 401, message = "Invalid username or password")
    })
    public ResponseEntity<Void> login(
            @ApiParam(required = true) @RequestParam String username,
            @ApiParam(required = true) @RequestParam String password,
            HttpServletRequest request
    ) {
        loginService.login(username, password);

        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_USERNAME, AuthContext.getUsername());
        session.setAttribute(SESSION_ROLE,     AuthContext.getRole());

        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password")
    @ApiOperation(value = "Change password")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Password changed successfully"),
            @ApiResponse(code = 401, message = "Invalid username or old password")
    })
    public ResponseEntity<Void> changePassword(
            @ApiParam(required = true) @RequestParam String username,
            @ApiParam(required = true) @RequestParam String oldPassword,
            @ApiParam(required = true) @RequestParam String newPassword
    ) {
        loginService.changePassword(username, oldPassword, newPassword);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    @ApiOperation(value = "Logout")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Logout successful")
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