package com.gym_project.controller;

import com.gym_project.constants.RoutConstants;
import com.gym_project.dto.response.TrainingTypeResponseDto;
import com.gym_project.service.TrainingTypeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.TRAINING_TYPES)
@Tag(name = "Training Type Management")
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;

    public TrainingTypeController(TrainingTypeService trainingTypeService) {
        this.trainingTypeService = trainingTypeService;
    }

    @GetMapping
    @Operation(summary = "Get all training types", description = "Returns the full list of available training types")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training types retrieved successfully")
    })
    public ResponseEntity<List<TrainingTypeResponseDto>> findAll() {
        List<TrainingTypeResponseDto> response = trainingTypeService.findAll();
        return ResponseEntity.ok(response);
    }
}