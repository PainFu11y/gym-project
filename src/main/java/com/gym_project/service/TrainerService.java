package com.gym_project.service;

import com.gym_project.dto.create.TrainerCreateDto;
import com.gym_project.dto.filter.TrainerTrainingFilterDto;
import com.gym_project.dto.response.TraineeResponseDto;
import com.gym_project.dto.response.TrainerResponseDto;
import com.gym_project.dto.response.TrainingResponseDto;
import com.gym_project.dto.update.TraineeTrainersUpdateDto;
import com.gym_project.dto.update.TrainerUpdateDto;

import java.util.List;

public interface TrainerService {

    TrainerResponseDto create(TrainerCreateDto dto);

    TrainerResponseDto getByUsername(String username);

    List<TrainerResponseDto> getAll();

    TrainerResponseDto update(String username, TrainerUpdateDto dto);

    void deleteByUsername(String username);

    TrainerResponseDto toggleActiveStatus(String username);

    void changePassword(String username, String newPassword);

    List<TrainingResponseDto> getTrainings(String trainerUsername, TrainerTrainingFilterDto filter);

    List<TrainerResponseDto> getUnassignedTrainersByTraineeUsername(String traineeUsername);

    List<TrainerResponseDto> updateTraineeTrainers(String traineeUsername, TraineeTrainersUpdateDto dto);

    TrainerResponseDto validateCredentials(String username, String password);

    List<TraineeResponseDto> getTrainees(String username);
}