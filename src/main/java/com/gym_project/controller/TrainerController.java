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

import javax.validation.Valid;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.TRAINERS)
@Api(tags = "Trainer Management")
public class TrainerController {

    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @PostMapping
    @ApiOperation(value = "Create a new trainer")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainer created successfully"),
            @ApiResponse(code = 400, message = "Invalid request body")
    })
    public ResponseEntity<TrainerCreateResponseDto> create(
            @ApiParam(required = true)
            @Valid @RequestBody TrainerCreateRequestDto dto) {

        TrainerCreateResponseDto response = trainerService.create(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}")
    @ApiOperation(value = "Get trainer by username")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainer found"),
            @ApiResponse(code = 404, message = "Trainer not found")
    })
    public ResponseEntity<TrainerResponseDto> getByUsername(
            @ApiParam(required = true)
            @PathVariable String username) {

        TrainerResponseDto response = trainerService.getByUsername(username);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @ApiOperation(value = "Update trainer profile")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainer updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request body"),
            @ApiResponse(code = 404, message = "Trainer not found")
    })
    public ResponseEntity<TrainerUpdateResponseDto> update(
            @ApiParam(required = true)
            @Valid @RequestBody TrainerUpdateRequestDto dto) {

        TrainerUpdateResponseDto response = trainerService.update(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unassigned/{username}")
    @ApiOperation(value = "Get unassigned active trainers")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainers retrieved successfully"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public ResponseEntity<List<TrainerSummaryDto>> getUnassignedActiveTrainers(
            @ApiParam(required = true)
            @PathVariable String username) {

        List<TrainerSummaryDto> trainers =
                trainerService.getUnassignedActiveTrainersByTraineeUsername(username);

        return ResponseEntity.ok(trainers);
    }

    @PostMapping("/trainings/filter")
    @ApiOperation(value = "Get trainer's trainings")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainings retrieved successfully"),
            @ApiResponse(code = 400, message = "Invalid filter parameters"),
            @ApiResponse(code = 404, message = "Trainer not found")
    })
    public ResponseEntity<List<TrainingResponseDto>> getTrainerTrainings(
            @RequestBody TrainerTrainingFilterDto dto) {

        List<TrainingResponseDto> trainings =
                trainerService.getTrainerTrainingsByFilter(dto);

        return ResponseEntity.ok(trainings);
    }

    @PatchMapping("/{username}/status")
    @ApiOperation(value = "Toggle trainer active status")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Status toggled successfully"),
            @ApiResponse(code = 404, message = "Trainer not found")
    })
    public ResponseEntity<Void> toggleStatus(
            @ApiParam(required = true)
            @PathVariable String username) {

        trainerService.toggleStatus(username);
        return ResponseEntity.ok().build();
    }
}