package com.gym_project.controller;

import com.gym_project.constants.RoutConstants;
import com.gym_project.dto.response.TrainingTypeResponseDto;
import com.gym_project.service.TrainingTypeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.TRAINING_TYPES)
@Api(tags = "Training Type Management")
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;

    public TrainingTypeController(TrainingTypeService trainingTypeService) {
        this.trainingTypeService = trainingTypeService;
    }

    @GetMapping
    @ApiOperation(value = "Get all training types", notes = "Returns the full list of available training types")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Training types retrieved successfully")
    })
    public ResponseEntity<List<TrainingTypeResponseDto>> findAll() {
        List<TrainingTypeResponseDto> response = trainingTypeService.findAll();
        return ResponseEntity.ok(response);
    }
}