package com.gym_project.security;

import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.exception.InvalidCredentialsException;
import com.gym_project.repository.TraineeRepository;
import com.gym_project.repository.TrainerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class LoginService {

    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final AuthService authService;

    public LoginService(
            TrainerRepository trainerRepository,
            TraineeRepository traineeRepository,
            AuthService authService
    ) {
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.authService = authService;
    }

    public void login(String username, String password) {
        log.debug("Login attempt for username='{}'", username);

        Optional<Trainer> trainer = trainerRepository.findByUsernameAndPassword(username, password);

        if (trainer.isPresent()) {
            authService.authenticate(username, Role.TRAINER);
            log.info("Login successful: username='{}', role=TRAINER", username);
            return;
        }

        Optional<Trainee> trainee = traineeRepository.findByUsername(username);

        if (trainee.isPresent() && trainee.get().getPassword().equals(password)) {
            authService.authenticate(username, Role.TRAINEE);
            log.info("Login successful: username='{}', role=TRAINEE", username);
            return;
        }

        log.warn("Login failed - invalid credentials for username='{}'", username);
        throw new InvalidCredentialsException();
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        log.debug("Password change attempt for username='{}'", username);

        Optional<Trainer> trainer = trainerRepository.findByUsernameAndPassword(username, oldPassword);

        if (trainer.isPresent()) {
            trainerRepository.changePassword(username, newPassword);
            log.info("Password changed for trainer username='{}'", username);
            return;
        }

        Optional<Trainee> trainee = traineeRepository.findByUsername(username);

        if (trainee.isPresent() && trainee.get().getPassword().equals(oldPassword)) {
            traineeRepository.changePassword(username, newPassword);
            log.info("Password changed for trainee username='{}'", username);
            return;
        }

        log.warn("Password change failed - invalid credentials for username='{}'", username);
        throw new InvalidCredentialsException();
    }
}