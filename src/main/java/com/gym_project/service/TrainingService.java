package com.gym_project.service;

import com.gym_project.dto.create.TrainingCreateDto;
import com.gym_project.dto.response.TrainingResponseDto;

public interface TrainingService {

    TrainingResponseDto create(TrainingCreateDto dto);
}