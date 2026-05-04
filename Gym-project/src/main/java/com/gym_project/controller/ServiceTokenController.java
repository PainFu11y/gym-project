package com.gym_project.controller;

import com.gym_project.constants.RoutConstants;
import com.gym_project.security.ServiceJwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(RoutConstants.BASE_URL + "/token")
@RequiredArgsConstructor
@Tag(name = "Service Token")
public class ServiceTokenController {

    private final ServiceJwtService serviceJwtService;

    @GetMapping("/service-token")
    @Operation(
        summary = "Issue a service JWT",
        description = "Returns a signed service token (type=SERVICE) for microservice-to-microservice calls. " +
                      "Must be authenticated as a user to obtain it."
    )
    public ResponseEntity<Map<String, String>> getServiceToken() {
        String token = serviceJwtService.generateServiceToken();
        return ResponseEntity.ok(Map.of("serviceToken", token));
    }
}
