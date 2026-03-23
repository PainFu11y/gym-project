package com.gym_project.repository;

import com.gym_project.dto.filter.TrainerTrainingFilterDto;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.Training;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository {

    void save(Trainer trainer);

    Trainer update(Trainer trainer);

    void delete(Trainer trainer);

    List<Trainer> findAll();

    Optional<Trainer> findByUsernameAndPassword(String username, String password);

    Optional<Trainer> findByUsername(String username);

    void changePassword(String username, String newPassword);

    void deleteByUsername(String username);

    List<Trainer> findUnassignedActiveTrainersByTraineeUsername(String traineeUsername);

    List<String> findUsernamesStartingWith(String base);

    List<Trainee> findTraineesByTrainerUsername(String trainerUsername);

    List<Trainer> findTrainersByUsernames(List<String> usernames);

    void toggleStatus(String username);
}