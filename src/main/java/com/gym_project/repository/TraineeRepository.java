package com.gym_project.repository;

import com.gym_project.entity.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeRepository {

    void save(Trainee trainee);

    Trainee update(Trainee trainee);

    void delete(Trainee trainee);

    List<Trainee> findAll();

    Optional<Trainee> findByUsername(String username);

    boolean existsByUsername(String username);

    List<String> findUsernamesStartingWith(String prefix);

    void deleteByUsername(String username);

    void changePassword(String username, String newPassword);

    void toggleStatus(String username);
}