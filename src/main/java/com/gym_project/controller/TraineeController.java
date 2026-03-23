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

import javax.validation.Valid;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.TRAINEES)
@Api(tags = "Trainee Management")
public class TraineeController {

    private final TraineeService traineeService;

    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @PostMapping
    @ApiOperation("Create new Trainee")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee created successfully"),
            @ApiResponse(code = 400, message = "Invalid request body")
    })
    public ResponseEntity<TraineeCreateResponseDto> create(
            @Valid @RequestBody TraineeCreateRequestDto dto
    ) {
        return ResponseEntity.ok(traineeService.create(dto));
    }

    @GetMapping("/{username}")
    @ApiOperation("Get trainee by username")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee found"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public ResponseEntity<TraineeResponseDto> getByUsername(
            @ApiParam(required = true)
            @PathVariable String username
    ) {
        return ResponseEntity.ok(traineeService.getByUsername(username));
    }

    @PutMapping
    @ApiOperation(value = "Update trainee profile")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request body"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public ResponseEntity<TraineeResponseDto> update(
            @Valid @RequestBody TraineeUpdateRequestDto dto
    ) {
        return ResponseEntity.ok(traineeService.update(dto));
    }

    @DeleteMapping("/{username}")
    @ApiOperation(value = "Delete trainee by username")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee deleted successfully"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public ResponseEntity<Void> delete(
            @PathVariable String username
    ) {
        traineeService.deleteByUsername(username);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/trainers")
    @ApiOperation(value = "Update trainee's trainer list")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainer list updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request body"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public ResponseEntity<List<TrainerSummaryDto>> updateTrainerList(
            @Valid @RequestBody UpdateTraineeTrainerListRequestDto dto
    ) {
        return ResponseEntity.ok(traineeService.updateTrainerList(dto));
    }

    @PostMapping("/trainings/filter")
    @ApiOperation(value = "Get trainee's trainings")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainings retrieved successfully"),
            @ApiResponse(code = 400, message = "Invalid filter parameters"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public ResponseEntity<List<TrainingResponseDto>> getTrainings(
            @ApiParam(value = "Filter criteria (username, date range, trainer name, training type)", required = true)
            @Valid @RequestBody TraineeTrainingsFilterRequestDto filter
    ) {
        return ResponseEntity.ok(
                traineeService.getTraineeTrainings(filter)
        );
    }

    @PatchMapping("/{username}/status")
    @ApiOperation(value = "Toggle trainee active status")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Status toggled successfully"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public ResponseEntity<Void> toggleStatus(
            @ApiParam(required = true)
            @PathVariable String username
    ) {
        traineeService.toggleStatus(username);
        return ResponseEntity.ok().build();
    }
}