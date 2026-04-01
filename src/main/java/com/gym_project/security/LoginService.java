package com.gym_project.security;

import com.gym_project.actuator.metrics.GymMetrics;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.exception.InvalidCredentialsException;
import com.gym_project.repository.TraineeRepository;
import com.gym_project.repository.TrainerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class LoginService {

    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final AuthService       authService;
    private final GymMetrics        gymMetrics;
    private final PasswordEncoder   passwordEncoder;

    public LoginService(
            TrainerRepository trainerRepository,
            TraineeRepository traineeRepository,
            AuthService authService,
            GymMetrics gymMetrics,
            PasswordEncoder passwordEncoder
    ) {
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.authService       = authService;
        this.gymMetrics        = gymMetrics;
        this.passwordEncoder   = passwordEncoder;
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public void login(String username, String password) {
        log.debug("Login attempt for username='{}'", username);

        Optional<Trainer> trainer = trainerRepository.findByUsername(username);
        if (trainer.isPresent()) {
            if (passwordEncoder.matches(password, trainer.get().getPassword())) {
                authService.authenticate(username, Role.TRAINER);
                log.info("Login successful: username='{}', role=TRAINER", username);
                gymMetrics.recordLoginSuccess();
                return;
            }
            log.warn("Login failed - wrong password for trainer username='{}'", username);
            gymMetrics.recordLoginFailure();
            throw new InvalidCredentialsException();
        }

        Optional<Trainee> trainee = traineeRepository.findByUsername(username);
        if (trainee.isPresent()) {
            if (passwordEncoder.matches(password, trainee.get().getPassword())) {
                authService.authenticate(username, Role.TRAINEE);
                log.info("Login successful: username='{}', role=TRAINEE", username);
                gymMetrics.recordLoginSuccess();
                return;
            }
            log.warn("Login failed - wrong password for trainee username='{}'", username);
            gymMetrics.recordLoginFailure();
            throw new InvalidCredentialsException();
        }

        log.warn("Login failed - user not found for username='{}'", username);
        gymMetrics.recordLoginFailure();
        throw new InvalidCredentialsException();
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        log.debug("Password change attempt for username='{}'", username);

        Optional<Trainer> trainer = trainerRepository.findByUsername(username);
        if (trainer.isPresent()) {
            if (!passwordEncoder.matches(oldPassword, trainer.get().getPassword())) {
                log.warn("Password change failed - wrong old password for trainer username='{}'", username);
                throw new InvalidCredentialsException();
            }
            trainerRepository.changePassword(username, passwordEncoder.encode(newPassword));
            log.info("Password changed for trainer username='{}'", username);
            return;
        }

        Optional<Trainee> trainee = traineeRepository.findByUsername(username);
        if (trainee.isPresent()) {
            if (!passwordEncoder.matches(oldPassword, trainee.get().getPassword())) {
                log.warn("Password change failed - wrong old password for trainee username='{}'", username);
                throw new InvalidCredentialsException();
            }
            traineeRepository.changePassword(username, passwordEncoder.encode(newPassword));
            log.info("Password changed for trainee username='{}'", username);
            return;
        }

        log.warn("Password change failed - user not found for username='{}'", username);
        throw new InvalidCredentialsException();
    }
}