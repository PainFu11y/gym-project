package com.gym_project.service.impl;

import com.gym_project.dto.create.TrainingCreateDto;
import com.gym_project.dto.response.TrainingResponseDto;
import com.gym_project.entity.*;
import com.gym_project.mapper.TrainingMapper;
import com.gym_project.repository.*;
import com.gym_project.service.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    public TrainingResponseDto create(TrainingCreateDto dto) {

        log.info("Creating training: trainee={}, trainer={}, typeId={}",
                dto.getTraineeUsername(),
                dto.getTrainerUsername(),
                dto.getTrainingTypeId());

        validate(dto);

        Trainee trainee = traineeRepository.findByUsername(dto.getTraineeUsername())
                .orElseThrow(() -> {
                    log.error("Trainee not found: {}", dto.getTraineeUsername());
                    return new RuntimeException("Trainee not found");
                });

        Trainer trainer = trainerRepository.findByUsername(dto.getTrainerUsername())
                .orElseThrow(() -> {
                    log.error("Trainer not found: {}", dto.getTrainerUsername());
                    return new RuntimeException("Trainer not found");
                });

        TrainingType trainingType = trainingTypeRepository.findById(dto.getTrainingTypeId())
                .orElseThrow(() -> {
                    log.error("Training type not found: {}", dto.getTrainingTypeId());
                    return new RuntimeException("Training type not found");
                });

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
        training.setTrainingName(dto.getTrainingName());
        training.setTrainingDate(dto.getTrainingDate());
        training.setTrainingDuration(dto.getTrainingDuration());

        if (!trainer.getTrainees().contains(trainee)) {
            trainer.getTrainees().add(trainee);
            trainee.getTrainers().add(trainer);
            log.debug("Linked trainer {} with trainee {}",
                    trainer.getUsername(), trainee.getUsername());
        }

        trainingRepository.save(training);

        log.info("Training successfully created: id={}, name={}",
                training.getId(), training.getTrainingName());

        return TrainingMapper.toDto(training);
    }

    private void validate(TrainingCreateDto dto) {

        if (dto.getTraineeUsername() == null || dto.getTraineeUsername().isBlank()) {
            log.error("Validation failed: trainee username is blank");
            throw new IllegalArgumentException("Trainee username is required");
        }

        if (dto.getTrainerUsername() == null || dto.getTrainerUsername().isBlank()) {
            log.error("Validation failed: trainer username is blank");
            throw new IllegalArgumentException("Trainer username is required");
        }

        if (dto.getTrainingTypeId() == null) {
            log.error("Validation failed: training type id is null");
            throw new IllegalArgumentException("Training type is required");
        }

        if (dto.getTrainingName() == null || dto.getTrainingName().isBlank()) {
            log.error("Validation failed: training name is blank");
            throw new IllegalArgumentException("Training name is required");
        }

        if (dto.getTrainingDate() == null) {
            log.error("Validation failed: training date is null");
            throw new IllegalArgumentException("Training date is required");
        }

        if (dto.getTrainingDuration() == null || dto.getTrainingDuration() <= 0) {
            log.error("Validation failed: training duration invalid");
            throw new IllegalArgumentException("Training duration must be positive");
        }
    }
}