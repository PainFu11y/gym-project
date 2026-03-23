package com.gym_project.security;

import com.gym_project.exception.AccessDeniedException;
import com.gym_project.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Slf4j
@Aspect
@Component
public class PreAuthorizeAspect {

    @Before("@annotation(preAuthorize)")
    public void check(JoinPoint jp, PreAuthorize preAuthorize) {
        if (!AuthContext.isAuthenticated()) {
            throw new UnauthorizedException("Authentication required");
        }

        String expr = preAuthorize.value();
        Object[] args = jp.getArgs();
        Parameter[] params = ((MethodSignature) jp.getSignature()).getMethod().getParameters();

        if (!evaluate(expr, args, params)) {
            log.warn("Access denied for '{}' on {}", AuthContext.getUsername(), jp.getSignature());
            throw new AccessDeniedException("Access denied");
        }
    }

    private boolean evaluate(String expr, Object[] args, Parameter[] params) {
        expr = expr.trim();


        if (expr.startsWith("hasRole(")) {
            String role = expr.replaceAll("hasRole\\(['\"](.+)['\"]\\)", "$1");
            return AuthContext.getRole() != null &&
                    AuthContext.getRole().asAuthority().equals(role);
        }

        if (expr.contains(" and ")) {
            String[] parts = expr.split(" and ", 2);
            return evaluate(parts[0], args, params) && evaluate(parts[1], args, params);
        }

        if (expr.contains(" or ")) {
            String[] parts = expr.split(" or ", 2);
            return evaluate(parts[0], args, params) || evaluate(parts[1], args, params);
        }

        if (expr.contains("== authentication.name")) {
            String paramExpr = expr.replace("== authentication.name", "").trim();
            return AuthContext.getUsername().equals(resolve(paramExpr, args, params));
        }

        return false;
    }

    private String resolve(String expr, Object[] args, Parameter[] params) {
        if (!expr.startsWith("#")) return null;
        expr = expr.substring(1).replace("(", "").replace(")", "").trim();

        if (expr.contains(".")) {
            String[] parts = expr.split("\\.", 2);
            Object obj = findArg(parts[0], args, params);
            return obj == null ? null : getter(obj, parts[1]);
        }
        Object val = findArg(expr, args, params);
        return val instanceof String ? (String) val : null;
    }

    private Object findArg(String name, Object[] args, Parameter[] params) {
        for (int i = 0; i < params.length; i++) {
            if (params[i].getName().equals(name)) return args[i];
        }
        return null;
    }

    private String getter(Object obj, String field) {
        try {
            Method m = obj.getClass().getMethod(
                    "get" + Character.toUpperCase(field.charAt(0)) + field.substring(1));
            Object v = m.invoke(obj);
            return v instanceof String ? (String) v : null;
        } catch (Exception e) {
            return null;
        }
    }
}