package com.gym_project.service.impl;

import com.gym_project.actuator.metrics.GymMetrics;
import com.gym_project.dto.create.request.TrainingCreateRequestDto;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.Training;
import com.gym_project.exception.EntityNotFoundException;
import com.gym_project.exception.ForbiddenOperationException;
import com.gym_project.messaging.WorkloadMessage;
import com.gym_project.messaging.WorkloadMessageProducer;
import com.gym_project.repository.TraineeRepository;
import com.gym_project.repository.TrainerRepository;
import com.gym_project.repository.TrainingRepository;
import com.gym_project.service.TrainingService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository     trainingRepository;
    private final TrainerRepository      trainerRepository;
    private final TraineeRepository      traineeRepository;
    private final WorkloadMessageProducer workloadProducer;
    private final GymMetrics             gymMetrics;

    public TrainingServiceImpl(
            TrainingRepository trainingRepository,
            TrainerRepository trainerRepository,
            TraineeRepository traineeRepository,
            WorkloadMessageProducer workloadProducer,
            GymMetrics gymMetrics
    ) {
        this.trainingRepository = trainingRepository;
        this.trainerRepository  = trainerRepository;
        this.traineeRepository  = traineeRepository;
        this.workloadProducer   = workloadProducer;
        this.gymMetrics         = gymMetrics;
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
        gymMetrics.recordTrainingCreated();

        workloadProducer.send(WorkloadMessage.builder()
                .trainerUsername(trainer.getUsername())
                .trainerFirstName(trainer.getFirstName())
                .trainerLastName(trainer.getLastName())
                .isActive(trainer.isActive())
                .trainingDate(dto.getTrainingDate())
                .trainingDuration(dto.getTrainingDuration())
                .actionType(WorkloadMessage.ActionType.ADD)
                .transactionId(MDC.get("transactionId"))
                .build());
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('TRAINER')")
    public void delete(Long trainingId) {
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException("Training not found: " + trainingId));

        Trainer trainer = training.getTrainer();

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!trainer.getUsername().equals(currentUser)) {
            throw new ForbiddenOperationException("You can only delete your own trainings");
        }

        trainingRepository.delete(trainingId);

        workloadProducer.send(WorkloadMessage.builder()
                .trainerUsername(trainer.getUsername())
                .trainerFirstName(trainer.getFirstName())
                .trainerLastName(trainer.getLastName())
                .isActive(trainer.isActive())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .actionType(WorkloadMessage.ActionType.DELETE)
                .transactionId(MDC.get("transactionId"))
                .build());
    }
}
