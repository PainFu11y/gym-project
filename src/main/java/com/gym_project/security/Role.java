package com.gym_project.security;

public enum Role {

    TRAINER,
    TRAINEE;

    public String asAuthority() {
        return "ROLE_" + this.name();
    }
}