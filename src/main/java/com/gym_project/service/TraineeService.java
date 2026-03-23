package com.gym_project.service;

import com.gym_project.dto.create.TraineeCreateDto;
import com.gym_project.dto.filter.TraineeTrainingFilterDto;
import com.gym_project.dto.response.TraineeResponseDto;
import com.gym_project.dto.response.TrainerResponseDto;
import com.gym_project.dto.response.TrainingResponseDto;
import com.gym_project.dto.update.TraineeUpdateDto;

import java.util.List;

public interface TraineeService {

    TraineeResponseDto create(TraineeCreateDto dto);

    TraineeResponseDto getByUsername(String username);

    List<TraineeResponseDto> getAll();

    TraineeResponseDto update(String username, TraineeUpdateDto dto);

    void deleteByUsername(String username);

    TraineeResponseDto toggleActiveStatus(String username);

    void changePassword(String username, String newPassword);

    List<TrainingResponseDto> getTrainings(String traineeUsername, TraineeTrainingFilterDto filter);

    TraineeResponseDto validateCredentials(String username, String password);

    List<TrainerResponseDto> getTrainers(String username);
}