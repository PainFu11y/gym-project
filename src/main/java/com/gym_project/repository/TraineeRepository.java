package com.gym_project.repository;

import com.gym_project.dto.filter.TraineeTrainingFilterDto;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.Training;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TraineeRepository {

    void save(Trainee trainee);

    Trainee update(Trainee trainee);

    void delete(Trainee trainee);

    Optional<Trainee> findById(Long id);

    List<Trainee> findAll();


    Optional<Trainee> findByUsername(String username);

    boolean existsByUsername(String username);

    List<String> findUsernamesStartingWith(String prefix);

    void deleteByUsername(String username);

    void changePassword(String username, String newPassword);

    List<Training> findTrainingsByTraineeAndFilter(String traineeUsername, TraineeTrainingFilterDto filter);

    List<Trainer> findTrainersByTraineeUsername(String traineeUsername);
}