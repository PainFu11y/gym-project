package com.gym_project.service.impl;

import com.gym_project.dto.create.request.TrainingCreateRequestDto;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.TrainingType;
import com.gym_project.exception.EntityNotFoundException;
import com.gym_project.repository.TraineeRepository;
import com.gym_project.repository.TrainerRepository;
import com.gym_project.repository.TrainingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock private TrainingRepository trainingRepository;
    @Mock private TrainerRepository trainerRepository;
    @Mock private TraineeRepository traineeRepository;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Trainee trainee;
    private Trainer trainer;
    private TrainingCreateRequestDto dto;

    @BeforeEach
    void setUp() {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Yoga");

        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUsername("Jane.Doe");
        trainee.setPassword("pass456");

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUsername("John.Smith");
        trainer.setPassword("pass123");
        trainer.setSpecialization(trainingType);
        trainer.setTrainees(new HashSet<>());

        dto = new TrainingCreateRequestDto();
        dto.setTraineeUsername("Jane.Doe");
        dto.setTrainerUsername("John.Smith");
        dto.setTrainingName("Morning Yoga");
        dto.setTrainingDate(LocalDate.now());
        dto.setTrainingDuration(60);
    }

    @Test
    void create_shouldSaveTrainingAndLinkTraineeToTrainer() {
        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("John.Smith")).thenReturn(Optional.of(trainer));

        trainingService.create(dto);

        verify(trainingRepository).save(argThat(t ->
                t.getTrainingName().equals("Morning Yoga") &&
                        t.getTrainee().equals(trainee) &&
                        t.getTrainer().equals(trainer) &&
                        t.getTrainingDuration().equals(60) &&
                        t.getTrainingType().equals(trainer.getSpecialization())
        ));
        assertThat(trainer.getTrainees()).contains(trainee);
    }

    @Test
    void create_shouldSetTrainingTypeFromTrainerSpecialization() {
        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("John.Smith")).thenReturn(Optional.of(trainer));

        trainingService.create(dto);

        ArgumentCaptor<com.gym_project.entity.Training> captor =
                ArgumentCaptor.forClass(com.gym_project.entity.Training.class);
        verify(trainingRepository).save(captor.capture());

        assertThat(captor.getValue().getTrainingType())
                .isEqualTo(trainer.getSpecialization());
        assertThat(captor.getValue().getTrainingType().getTrainingTypeName())
                .isEqualTo("Yoga");
    }

    @Test
    void create_shouldThrowEntityNotFoundException_whenTraineeNotFound() {
        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.create(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Jane.Doe");

        verify(trainerRepository, never()).findByUsername(any());
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowEntityNotFoundException_whenTrainerNotFound() {
        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("John.Smith")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.create(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("John.Smith");

        verify(trainingRepository, never()).save(any());
    }

    @Test
    void create_shouldNotLinkTraineeToTrainer_whenSaveFails() {
        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("John.Smith")).thenReturn(Optional.of(trainer));
        doThrow(new RuntimeException("DB error")).when(trainingRepository).save(any());

        assertThatThrownBy(() -> trainingService.create(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DB error");

        assertThat(trainer.getTrainees()).doesNotContain(trainee);
    }
}