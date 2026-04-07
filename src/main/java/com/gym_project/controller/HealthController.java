package com.gym_project.controller;

import com.gym_project.actuator.health.DatabaseHealthIndicator;
import com.gym_project.actuator.health.GymUsersHealthIndicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@Tag(name = "Health")
public class HealthController {

    private final DatabaseHealthIndicator databaseHealthIndicator;
    private final GymUsersHealthIndicator gymUsersHealthIndicator;

    @GetMapping
    @Operation(summary = "Get application health status",
            description = "Returns the status of all custom health indicators")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Application is healthy"),
            @ApiResponse(responseCode = "503", description = "One or more components are down")
    })
    public ResponseEntity<Map<String, Object>> health() {
        Health dbHealth      = databaseHealthIndicator.health();
        Health gymUsersHealth = gymUsersHealthIndicator.health();

        boolean allUp = isUp(dbHealth) && isUp(gymUsersHealth);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", allUp ? "UP" : "DOWN");
        response.put("components", Map.of(
                "database", toMap(dbHealth),
                "gymUsers", toMap(gymUsersHealth)
        ));

        return allUp
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(503).body(response);
    }

    @GetMapping("/database")
    @Operation(summary = "Database health check")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Database is reachable"),
            @ApiResponse(responseCode = "503", description = "Database is unreachable")
    })
    public ResponseEntity<Map<String, Object>> databaseHealth() {
        Health health = databaseHealthIndicator.health();
        return toResponse(health);
    }

    @GetMapping("/gym-users")
    @Operation(summary = "Gym users health check")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Active users exist"),
            @ApiResponse(responseCode = "503", description = "No active users found")
    })
    public ResponseEntity<Map<String, Object>> gymUsersHealth() {
        Health health = gymUsersHealthIndicator.health();
        return toResponse(health);
    }

    private boolean isUp(Health health) {
        return "UP".equals(health.getStatus().getCode());
    }

    private Map<String, Object> toMap(Health health) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", health.getStatus().getCode());
        map.putAll(health.getDetails());
        return map;
    }

    private ResponseEntity<Map<String, Object>> toResponse(Health health) {
        Map<String, Object> body = toMap(health);
        return isUp(health)
                ? ResponseEntity.ok(body)
                : ResponseEntity.status(503).body(body);
    }
}