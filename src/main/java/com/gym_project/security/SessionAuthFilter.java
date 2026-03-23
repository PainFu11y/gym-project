package com.gym_project.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@Component("sessionAuthFilter")
public class SessionAuthFilter extends OncePerRequestFilter {

    private static final String SESSION_USERNAME = "AUTH_USERNAME";
    private static final String SESSION_ROLE     = "AUTH_ROLE";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path   = request.getRequestURI();
        String method = request.getMethod();

        log.debug("Incoming request: {} {}", method, path);

        if (isPublic(path, method)) {
            log.debug("Public path, skipping auth: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            String username = (String) session.getAttribute(SESSION_USERNAME);
            Role   role     = (Role)   session.getAttribute(SESSION_ROLE);
            if (username != null && role != null) {
                AuthContext.set(username, role);
            }
        }

        if (!AuthContext.isAuthenticated()) {
            log.warn("Unauthenticated request to {} {}", method, path);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        try {
            filterChain.doFilter(request, response);

            if (AuthContext.isAuthenticated()) {
                HttpSession s = request.getSession(true);
                s.setAttribute(SESSION_USERNAME, AuthContext.getUsername());
                s.setAttribute(SESSION_ROLE,     AuthContext.getRole());
            }
        } finally {
            AuthContext.clear();
        }
    }

    private boolean isPublic(String path, String method) {
        if (path.equals("/api/auth/login"))           return true;
        if (path.equals("/api/auth/change-password")) return true;

        if ("POST".equalsIgnoreCase(method) && path.equals("/api/trainees")) return true;
        if ("POST".equalsIgnoreCase(method) && path.equals("/api/trainers"))  return true;

        if (path.equals("/swagger-ui.html"))          return true;
        if (path.startsWith("/swagger-resources"))    return true;
        if (path.startsWith("/webjars/"))             return true;
        if (path.startsWith("/v2/api-docs"))          return true;
        if (path.startsWith("/configuration/"))       return true;

        if (path.equals("/favicon.ico"))              return true;

        return false;
    }
}