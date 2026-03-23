package com.gym_project.service;

import com.gym_project.dto.create.request.TraineeCreateRequestDto;
import com.gym_project.dto.create.response.TraineeCreateResponseDto;
import com.gym_project.dto.request.LoginRequestDto;
import com.gym_project.dto.request.TraineeTrainingsFilterRequestDto;
import com.gym_project.dto.response.TraineeResponseDto;
import com.gym_project.dto.response.TrainerResponseDto;
import com.gym_project.dto.response.TrainerSummaryDto;
import com.gym_project.dto.response.TrainingResponseDto;
import com.gym_project.dto.update.request.TraineeUpdateRequestDto;
import com.gym_project.dto.update.request.UpdateTraineeTrainerListRequestDto;

import java.util.List;

public interface TraineeService {

    TraineeCreateResponseDto create(TraineeCreateRequestDto dto);

    TraineeResponseDto getByUsername(String username);

    TraineeResponseDto update(TraineeUpdateRequestDto dto);

    void deleteByUsername(String username);

    void toggleStatus(String username);

    List<TrainingResponseDto> getTraineeTrainings(TraineeTrainingsFilterRequestDto filter);

    TraineeResponseDto validateCredentials(LoginRequestDto dto);

    List<TrainerResponseDto> getTrainers(String username);

    List<TrainerSummaryDto> updateTrainerList(UpdateTraineeTrainerListRequestDto dto);
}