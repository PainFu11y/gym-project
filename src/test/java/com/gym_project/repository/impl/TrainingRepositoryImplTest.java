package com.gym_project.repository.impl;

import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.Training;
import com.gym_project.entity.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingRepositoryImplTest {

    @Mock private EntityManager entityManager;

    @InjectMocks
    private TrainingRepositoryImpl trainingRepository;

    private Training training;

    @BeforeEach
    void setUp() {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Yoga");

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUsername("Jane.Doe");

        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUsername("John.Smith");
        trainer.setSpecialization(trainingType);

        training = new Training();
        training.setId(1L);
        training.setTrainingName("Morning Yoga");
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
    }

    @Test
    void save_shouldPersistTraining() {
        trainingRepository.save(training);

        verify(entityManager).persist(training);
    }

    @Test
    void update_shouldMergeAndReturnTraining() {
        when(entityManager.merge(training)).thenReturn(training);

        Training result = trainingRepository.update(training);

        assertThat(result).isEqualTo(training);
        verify(entityManager).merge(training);
    }
}