package com.gym_project.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    public void authenticate(String username, Role role) {

        List<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority(role.asAuthority()));

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        System.out.println("Authenticated as " + role);
    }

    public void logout() {
        SecurityContextHolder.clearContext();
        System.out.println("Logged out");
    }
}