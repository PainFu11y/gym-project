package com.gym_project.security;

public class AuthContext {

    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
    private static final ThreadLocal<Role> ROLE = new ThreadLocal<>();

    public static void set(String username, Role role) {
        USERNAME.set(username);
        ROLE.set(role);
    }

    public static String getUsername() { return USERNAME.get(); }
    public static Role getRole()       { return ROLE.get(); }
    public static boolean isAuthenticated() { return USERNAME.get() != null; }

    public static void clear() {
        USERNAME.remove();
        ROLE.remove();
    }
}