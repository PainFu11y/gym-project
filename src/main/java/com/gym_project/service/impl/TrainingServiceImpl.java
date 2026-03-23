package com.gym_project.service.impl;

import com.gym_project.dto.create.request.TrainingCreateRequestDto;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.Training;
import com.gym_project.exception.EntityNotFoundException;
import com.gym_project.repository.TraineeRepository;
import com.gym_project.repository.TrainerRepository;
import com.gym_project.repository.TrainingRepository;
import com.gym_project.service.TrainingService;
import lombok.extern.slf4j.Slf4j;
import com.gym_project.security.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;

    public TrainingServiceImpl(
            TrainingRepository trainingRepository,
            TrainerRepository trainerRepository,
            TraineeRepository traineeRepository
    ) {
        this.trainingRepository = trainingRepository;
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('TRAINER') and #dto.trainerUsername == authentication.name")
    public void create(TrainingCreateRequestDto dto) {
        log.debug("Creating training '{}' for trainee='{}' by trainer='{}'",
                dto.getTrainingName(), dto.getTraineeUsername(), dto.getTrainerUsername());

        Trainee trainee = traineeRepository.findByUsername(dto.getTraineeUsername())
                .orElseThrow(() -> {
                    log.warn("Trainee not found: username='{}'", dto.getTraineeUsername());
                    return new EntityNotFoundException("Trainee not found: " + dto.getTraineeUsername());
                });

        Trainer trainer = trainerRepository.findByUsername(dto.getTrainerUsername())
                .orElseThrow(() -> {
                    log.warn("Trainer not found: username='{}'", dto.getTrainerUsername());
                    return new EntityNotFoundException("Trainer not found: " + dto.getTrainerUsername());
                });

        Training training = new Training();
        training.setTrainingName(dto.getTrainingName());
        training.setTrainingDate(dto.getTrainingDate());
        training.setTrainingDuration(dto.getTrainingDuration());
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingType(trainer.getSpecialization());

        trainingRepository.save(training);
        trainer.getTrainees().add(trainee);

        log.info("Training created: name='{}', trainee='{}', trainer='{}', date={}, duration={}",
                dto.getTrainingName(), dto.getTraineeUsername(), dto.getTrainerUsername(),
                dto.getTrainingDate(), dto.getTrainingDuration());
    }
}