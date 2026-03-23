package com.gym_project.service;

import com.gym_project.dto.create.request.TrainerCreateRequestDto;
import com.gym_project.dto.create.response.TrainerCreateResponseDto;
import com.gym_project.dto.filter.TrainerTrainingFilterDto;
import com.gym_project.dto.response.TrainerResponseDto;
import com.gym_project.dto.response.TrainerSummaryDto;
import com.gym_project.dto.response.TrainingResponseDto;
import com.gym_project.dto.update.request.TrainerUpdateRequestDto;
import com.gym_project.dto.update.response.TrainerUpdateResponseDto;

import java.util.List;

public interface TrainerService {

    TrainerCreateResponseDto create(TrainerCreateRequestDto dto);

    TrainerResponseDto getByUsername(String username);

    TrainerUpdateResponseDto update(TrainerUpdateRequestDto dto);

    List<TrainerSummaryDto> getUnassignedActiveTrainersByTraineeUsername(String username);

    List<TrainingResponseDto> getTrainerTrainingsByFilter(TrainerTrainingFilterDto dto);

    void toggleStatus(String username);
}
