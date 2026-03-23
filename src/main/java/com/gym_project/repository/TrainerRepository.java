package com.gym_project.repository;

import com.gym_project.dto.filter.TrainerTrainingFilterDto;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.Training;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository {

    void save(Trainer trainer);

    Trainer update(Trainer trainer);

    void delete(Trainer trainer);

    Optional<Trainer> findById(Long id);

    List<Trainer> findAll();

    List<Trainer> findBySpecialization(String specialization);

    Optional<Trainer> findByUsernameAndPassword(String username, String password);

    Optional<Trainer> findByUsername(String username);

    void changePassword(String username, String newPassword);


    void deleteByUsername(String username);

    List<Trainer> findUnassignedTrainersByTraineeUsername(String traineeUsername);

    List<String> findUsernamesStartingWith(String base);

    List<Training> findTrainingsByTrainerAndFilter(String trainerUsername, TrainerTrainingFilterDto filter);

    List<Trainee> findTraineesByTrainerUsername(String trainerUsername);
}