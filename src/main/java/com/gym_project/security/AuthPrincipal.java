package com.gym_project.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthPrincipal {
    private final String username;
    private final Role role;
}