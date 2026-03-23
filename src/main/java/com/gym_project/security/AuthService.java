package com.gym_project.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    public void authenticate(String username, Role role) {
        AuthContext.set(username, role);
        log.info("Authenticated: username='{}', role={}", username, role);
    }

    public void logout() {
        log.info("Logged out: username='{}'", AuthContext.getUsername());
        AuthContext.clear();
    }
}