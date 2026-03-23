package com.gym_project.controller;

import com.gym_project.constants.RoutConstants;
import com.gym_project.dto.create.request.TrainingCreateRequestDto;
import com.gym_project.service.TrainingService;

import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.TRAININGS)
@Api(tags = "Training Management")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping
    @ApiOperation(value = "Create a new training session")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Training created successfully"),
            @ApiResponse(code = 400, message = "Invalid request body"),
            @ApiResponse(code = 404, message = "Trainee or trainer not found")
    })
    public ResponseEntity<Void> create(
            @Valid @RequestBody TrainingCreateRequestDto dto) {

        trainingService.create(dto);
        return ResponseEntity.ok().build();
    }
}