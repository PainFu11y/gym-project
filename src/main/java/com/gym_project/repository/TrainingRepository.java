package com.gym_project.repository;

import com.gym_project.dto.request.TraineeTrainingsFilterRequestDto;
import com.gym_project.dto.request.TrainerTrainingsRequestDto;
import com.gym_project.entity.Training;

import java.util.List;

public interface TrainingRepository {

    void save(Training training);

    Training update(Training training);

    List<Training> findByTraineeFilter(TraineeTrainingsFilterRequestDto trainee);

    List<Training> findByTrainerFilter(TrainerTrainingsRequestDto trainer);
}