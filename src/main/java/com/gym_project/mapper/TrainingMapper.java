package com.gym_project.mapper;

import com.gym_project.dto.response.TrainingResponseDto;
import com.gym_project.entity.Training;

public class TrainingMapper {

    public static TrainingResponseDto toDto(Training training) {

        TrainingResponseDto dto = new TrainingResponseDto();

        dto.setId(training.getId());
        dto.setTrainingName(training.getTrainingName());
        dto.setTrainingDate(training.getTrainingDate());
        dto.setTrainingDuration(training.getTrainingDuration());

        dto.setTraineeUsername(training.getTrainee().getUsername());
        dto.setTrainerUsername(training.getTrainer().getUsername());
        dto.setTrainingTypeName(training.getTrainingType().getTrainingTypeName());

        return dto;
    }
}