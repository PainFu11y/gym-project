package com.gym_project.controller;

import com.gym_project.constants.RoutConstants;
import com.gym_project.dto.create.request.TrainingCreateRequestDto;
import com.gym_project.service.TrainingService;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.TRAININGS)
@Tag(name = "Training Management")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping
    @Operation(summary = "Create a new training session")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Trainee or trainer not found")
    })
    public ResponseEntity<Void> create(
            @Valid @RequestBody TrainingCreateRequestDto dto) {

        trainingService.create(dto);
        return ResponseEntity.ok().build();
    }
}