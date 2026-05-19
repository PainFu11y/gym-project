package com.gym_project.controller;

import com.gym_project.exception.GlobalExceptionHandler;
import com.gym_project.exception.InvalidCredentialsException;
import com.gym_project.security.GymUserDetails;
import com.gym_project.security.JwtService;
import com.gym_project.security.LoginService;
import com.gym_project.security.TokenBlacklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService            jwtService;
    @Mock private LoginService          loginService;
    @Mock private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private LoginController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(controller, "blockDurationSeconds", 300L);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ------------------------------------------------------------------ login

    @Test
    void login_shouldReturn200WithToken_whenCredentialsAreValid() throws Exception {
        GymUserDetails userDetails = mock(GymUserDetails.class);
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token-value");

        mockMvc.perform(get("/api/auth/login")
                        .param("username", "john.doe")
                        .param("password", "secret"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-value"));

        verify(loginService).recordLoginSuccess();
    }

    @Test
    void login_shouldReturn423_whenAccountIsLocked() throws Exception {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new LockedException("Account locked"));

        mockMvc.perform(get("/api/auth/login")
                        .param("username", "locked.user")
                        .param("password", "any"))
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.error").value("Account locked.  Try again in 300 seconds."));

        verifyNoInteractions(jwtService);
    }

    @Test
    void login_shouldReturn401_whenCredentialsAreInvalid() throws Exception {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(get("/api/auth/login")
                        .param("username", "john.doe")
                        .param("password", "wrong"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid username or password"));

        verifyNoInteractions(jwtService);
        verifyNoInteractions(loginService);
    }

    // --------------------------------------------------------------- changePassword

    @Test
    void changePassword_shouldReturn200_whenOldPasswordIsCorrect() throws Exception {
        doNothing().when(loginService).changePassword("john.doe", "old", "new");

        mockMvc.perform(put("/api/auth/change-password")
                        .param("username", "john.doe")
                        .param("oldPassword", "old")
                        .param("newPassword", "new"))
                .andExpect(status().isOk());

        verify(loginService).changePassword("john.doe", "old", "new");
    }

    @Test
    void changePassword_shouldReturn401_whenOldPasswordIsWrong() throws Exception {
        doThrow(new InvalidCredentialsException())
                .when(loginService).changePassword("john.doe", "wrong", "new");

        mockMvc.perform(put("/api/auth/change-password")
                        .param("username", "john.doe")
                        .param("oldPassword", "wrong")
                        .param("newPassword", "new"))
                .andExpect(status().isUnauthorized());
    }

    // ------------------------------------------------------------------ logout

    @Test
    void logout_shouldReturn200_andBlacklistToken_whenBearerTokenPresent() throws Exception {
        when(jwtService.getExpirationMs()).thenReturn(3600000L);

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer my-jwt-token"))
                .andExpect(status().isOk());

        verify(tokenBlacklistService).blacklist("my-jwt-token", 3600000L);
    }

    @Test
    void logout_shouldReturn200_whenNoAuthorizationHeader() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk());

        verifyNoInteractions(tokenBlacklistService);
    }

    @Test
    void logout_shouldReturn200_whenAuthorizationHeaderIsNotBearer() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Basic dXNlcjpwYXNz"))
                .andExpect(status().isOk());

        verifyNoInteractions(tokenBlacklistService);
    }
}
