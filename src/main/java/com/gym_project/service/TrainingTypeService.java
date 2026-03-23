package com.gym_project.service;

import com.gym_project.dto.response.TrainingTypeResponseDto;

import java.util.List;

public interface TrainingTypeService {
    List<TrainingTypeResponseDto> findAll();
}
