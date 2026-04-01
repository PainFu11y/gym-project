package com.gym_project.controller;

import com.gym_project.constants.RoutConstants;
import com.gym_project.dto.create.request.TraineeCreateRequestDto;
import com.gym_project.dto.create.response.TraineeCreateResponseDto;
import com.gym_project.dto.request.TraineeTrainingsFilterRequestDto;
import com.gym_project.dto.response.TraineeResponseDto;
import com.gym_project.dto.response.TrainerSummaryDto;
import com.gym_project.dto.response.TrainingResponseDto;
import com.gym_project.dto.update.request.TraineeUpdateRequestDto;
import com.gym_project.dto.update.request.UpdateTraineeTrainerListRequestDto;
import com.gym_project.service.TraineeService;

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
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.TRAINEES)
@Tag(name = "Trainee Management")
public class TraineeController {

    private final TraineeService traineeService;

    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @PostMapping
    @Operation(summary = "Create new Trainee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public ResponseEntity<TraineeCreateResponseDto> create(
            @Valid @RequestBody TraineeCreateRequestDto dto
    ) {
        return ResponseEntity.ok(traineeService.create(dto));
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get trainee by username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee found"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    public ResponseEntity<TraineeResponseDto> getByUsername(
            @Parameter(required = true)
            @PathVariable String username
    ) {
        return ResponseEntity.ok(traineeService.getByUsername(username));
    }

    @PutMapping
    @Operation(summary = "Update trainee profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    public ResponseEntity<TraineeResponseDto> update(
            @Valid @RequestBody TraineeUpdateRequestDto dto
    ) {
        return ResponseEntity.ok(traineeService.update(dto));
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Delete trainee by username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    public ResponseEntity<Void> delete(
            @PathVariable String username
    ) {
        traineeService.deleteByUsername(username);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/trainers")
    @Operation(summary = "Update trainee's trainer list")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer list updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    public ResponseEntity<List<TrainerSummaryDto>> updateTrainerList(
            @Valid @RequestBody UpdateTraineeTrainerListRequestDto dto
    ) {
        return ResponseEntity.ok(traineeService.updateTrainerList(dto));
    }

    @PostMapping("/trainings/filter")
    @Operation(summary = "Get trainee's trainings")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    public ResponseEntity<List<TrainingResponseDto>> getTrainings(
            @Parameter(description = "Filter criteria (username, date range, trainer name, training type)", required = true)
            @Valid @RequestBody TraineeTrainingsFilterRequestDto filter
    ) {
        return ResponseEntity.ok(
                traineeService.getTraineeTrainings(filter)
        );
    }

    @PatchMapping("/{username}/status")
    @Operation(summary = "Toggle trainee active status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status toggled successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    public ResponseEntity<Void> toggleStatus(
            @Parameter(required = true)
            @PathVariable String username
    ) {
        traineeService.toggleStatus(username);
        return ResponseEntity.ok().build();
    }
}