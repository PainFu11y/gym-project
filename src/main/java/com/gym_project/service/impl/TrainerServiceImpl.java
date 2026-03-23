package com.gym_project.service.impl;

import com.gym_project.dto.create.TrainerCreateDto;
import com.gym_project.dto.filter.TrainerTrainingFilterDto;
import com.gym_project.dto.response.TraineeResponseDto;
import com.gym_project.dto.response.TrainerResponseDto;
import com.gym_project.dto.response.TrainingResponseDto;
import com.gym_project.dto.update.TraineeTrainersUpdateDto;
import com.gym_project.dto.update.TrainerUpdateDto;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.Training;
import com.gym_project.mapper.TraineeMapper;
import com.gym_project.mapper.TrainerMapper;
import com.gym_project.mapper.TrainingMapper;
import com.gym_project.utils.validation.TrainerValidator;
import lombok.extern.slf4j.Slf4j;
import com.gym_project.repository.TraineeRepository;
import com.gym_project.repository.TrainerRepository;
import com.gym_project.service.TrainerService;
import com.gym_project.utils.PasswordGenerator;
import com.gym_project.utils.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;

    @Override
    public TrainerResponseDto create(TrainerCreateDto dto) {

        log.info("Creating trainer: {} {}", dto.getFirstName(), dto.getLastName());

        TrainerValidator.validateCreate(dto);

        String base = dto.getFirstName() + "." + dto.getLastName();
        List<String> existingUsernames = trainerRepository.findUsernamesStartingWith(base);

        String generatedUsername = UsernameGenerator.generate(dto.getFirstName(), dto.getLastName(), existingUsernames);
        log.debug("Generated trainer username: {}", generatedUsername);

        Trainer trainer = TrainerMapper.toEntity(dto);
        trainer.setUsername(generatedUsername);
        trainer.setPassword(PasswordGenerator.generate());

        trainerRepository.save(trainer);

        log.info("Trainer created successfully: \nusername: {} \npassword: {}", generatedUsername, trainer.getPassword());

        return TrainerMapper.toDto(trainer);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("#username == authentication.name")
    public TrainerResponseDto getByUsername(String username) {

        log.debug("Fetching trainer by username: {}", username);

        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainer not found: {}", username);
                    return new RuntimeException("Trainer not found");
                });

        return TrainerMapper.toDto(trainer);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('TRAINER', 'TRAINEE')")
    public List<TrainerResponseDto> getAll() {
        log.debug("Fetching all trainers");
        return trainerRepository.findAll().stream()
                .map(TrainerMapper::toDto)
                .toList();
    }

    @Override
    public TrainerResponseDto update(String username, TrainerUpdateDto dto) {

        log.info("Updating trainer: {}", username);

        TrainerValidator.validateUpdate(dto);

        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainer not found for update: {}", username);
                    return new RuntimeException("Trainer not found");
                });

        TrainerMapper.updateEntity(trainer, dto);

        log.info("Trainer updated successfully: {}", username);

        return TrainerMapper.toDto(trainer);
    }

    @Override
    @PreAuthorize("#username == authentication.name")
    public void deleteByUsername(String username) {

        log.info("Deleting trainer: {}", username);

        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainer not found for deletion: {}", username);
                    return new RuntimeException("Trainer not found");
                });

        for (Trainee trainee : trainer.getTrainees()) {
            trainee.getTrainers().remove(trainer);
        }
        trainer.getTrainees().clear();

        trainerRepository.deleteByUsername(username);

        log.info("Trainer deleted: {}", username);
    }

    @Override
    @PreAuthorize("#username == authentication.name or hasRole('TRAINEE')")
    public TrainerResponseDto toggleActiveStatus(String username) {

        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        boolean newStatus = !trainer.isActive();
        trainer.setActive(newStatus);

        log.info("{} trainer: {}", newStatus ? "Activated" : "Deactivated", username);

        return TrainerMapper.toDto(trainer);
    }

    @Override
    @PreAuthorize("#username == authentication.name")
    public void changePassword(String username, String newPassword) {

        log.info("Changing password for trainer: {}", username);

        if (newPassword == null || newPassword.isBlank()) {
            log.warn("Attempt to set blank password for trainer: {}", username);
            throw new IllegalArgumentException("Password cannot be blank");
        }

        trainerRepository.changePassword(username, newPassword);

        log.info("Password changed successfully for trainer: {}", username);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("#username == authentication.name or hasRole('TRAINER')")
    public List<TrainingResponseDto> getTrainings(String trainerUsername, TrainerTrainingFilterDto filter) {
        List<Training> trainings = trainerRepository.findTrainingsByTrainerAndFilter(trainerUsername, filter);
        return trainings.stream()
                .map(TrainingMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('TRAINER')")
    public List<TrainerResponseDto> getUnassignedTrainersByTraineeUsername(String traineeUsername) {

        log.debug("Fetching unassigned trainers for trainee: {}", traineeUsername);

        if (traineeUsername == null || traineeUsername.isBlank()) {
            log.warn("Blank trainee username provided for unassigned trainers search");
            throw new IllegalArgumentException("Trainee username must not be blank");
        }

        List<Trainer> trainers =
                trainerRepository.findUnassignedTrainersByTraineeUsername(traineeUsername);

        log.info("Found {} unassigned trainers for trainee: {}",
                trainers.size(), traineeUsername);

        return trainers.stream()
                .map(TrainerMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    @PreAuthorize("#username == authentication.name or hasRole('TRAINER')")
    public List<TrainerResponseDto> updateTraineeTrainers(String traineeUsername,
                                                          TraineeTrainersUpdateDto dto) {

        log.info("Updating trainers for trainee: {}", traineeUsername);

        if (traineeUsername == null || traineeUsername.isBlank()) {
            log.warn("Blank trainee username in updateTraineeTrainers");
            throw new IllegalArgumentException("Trainee username must not be blank");
        }

        Trainee trainee = traineeRepository.findByUsername(traineeUsername)
                .orElseThrow(() -> {
                    log.warn("Trainee not found: {}", traineeUsername);
                    return new RuntimeException("Trainee not found");
                });

        if (trainee.getTrainers() != null) {
            for (Trainer trainer : trainee.getTrainers()) {
                trainer.getTrainees().remove(trainee);
            }
            trainee.getTrainers().clear();
        }

        List<Trainer> newTrainers = dto.getTrainerUsernames().stream()
                .map(username -> trainerRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Trainer not found: " + username)))
                .toList();

        for (Trainer trainer : newTrainers) {
            trainer.getTrainees().add(trainee);
            trainee.getTrainers().add(trainer);
        }

        log.info("Assigned {} trainers to trainee: {}",
                newTrainers.size(), traineeUsername);

        return newTrainers.stream()
                .map(TrainerMapper::toDto)
                .toList();
    }

    @Override
    public TrainerResponseDto validateCredentials(String username, String password) {

        log.debug("Validating credentials for trainer: {}", username);

        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Login failed - trainer not found: {}", username);
                    return new RuntimeException("Trainer not found");
                });

        if (!trainer.getPassword().equals(password)) {
            log.warn("Invalid password for trainer: {}", username);
            throw new RuntimeException("Invalid password");
        }

        log.info("Trainer successfully authenticated: {}", username);

        return TrainerMapper.toDto(trainer) ;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("#username == authentication.name")
    public List<TraineeResponseDto> getTrainees(String username) {

        log.debug("Fetching trainees for trainer: {}", username);

        List<Trainee> trainees =
                trainerRepository.findTraineesByTrainerUsername(username);

        return trainees.stream()
                .map(TraineeMapper::toDto)
                .toList();
    }
}