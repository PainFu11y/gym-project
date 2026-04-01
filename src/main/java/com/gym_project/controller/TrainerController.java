package com.gym_project.controller;

import com.gym_project.constants.RoutConstants;
import com.gym_project.dto.create.request.TrainerCreateRequestDto;
import com.gym_project.dto.create.response.TrainerCreateResponseDto;
import com.gym_project.dto.filter.TrainerTrainingFilterDto;
import com.gym_project.dto.response.TrainerResponseDto;
import com.gym_project.dto.response.TrainerSummaryDto;
import com.gym_project.dto.response.TrainingResponseDto;
import com.gym_project.dto.update.request.TrainerUpdateRequestDto;
import com.gym_project.dto.update.response.TrainerUpdateResponseDto;
import com.gym_project.service.TrainerService;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.TRAINERS)
@Tag(name = "Trainer Management")
public class TrainerController {

    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @PostMapping
    @Operation(summary = "Create a new trainer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public ResponseEntity<TrainerCreateResponseDto> create(
            @Parameter(required = true)
            @Valid @RequestBody TrainerCreateRequestDto dto) {

        TrainerCreateResponseDto response = trainerService.create(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get trainer by username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer found"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    public ResponseEntity<TrainerResponseDto> getByUsername(
            @Parameter(required = true)
            @PathVariable String username) {

        TrainerResponseDto response = trainerService.getByUsername(username);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @Operation(summary = "Update trainer profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    public ResponseEntity<TrainerUpdateResponseDto> update(
            @Parameter(required = true)
            @Valid @RequestBody TrainerUpdateRequestDto dto) {

        TrainerUpdateResponseDto response = trainerService.update(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unassigned/{username}")
    @Operation(summary = "Get unassigned active trainers")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainers retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    public ResponseEntity<List<TrainerSummaryDto>> getUnassignedActiveTrainers(
            @Parameter(required = true)
            @PathVariable String username) {

        List<TrainerSummaryDto> trainers =
                trainerService.getUnassignedActiveTrainersByTraineeUsername(username);

        return ResponseEntity.ok(trainers);
    }

    @PostMapping("/trainings/filter")
    @Operation(summary = "Get trainer's trainings")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    public ResponseEntity<List<TrainingResponseDto>> getTrainerTrainings(
            @RequestBody TrainerTrainingFilterDto dto) {

        List<TrainingResponseDto> trainings =
                trainerService.getTrainerTrainingsByFilter(dto);

        return ResponseEntity.ok(trainings);
    }

    @PatchMapping("/{username}/status")
    @Operation(summary = "Toggle trainer active status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status toggled successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    public ResponseEntity<Void> toggleStatus(
            @Parameter(required = true)
            @PathVariable String username) {

        trainerService.toggleStatus(username);
        return ResponseEntity.ok().build();
    }
}