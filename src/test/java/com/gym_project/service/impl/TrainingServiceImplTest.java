package com.gym_project.service.impl;

import com.gym_project.dto.create.TrainingCreateDto;
import com.gym_project.dto.response.TrainingResponseDto;
import com.gym_project.entity.*;
import com.gym_project.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceImplTest {

    private TrainingRepository trainingRepository;
    private TraineeRepository traineeRepository;
    private TrainerRepository trainerRepository;
    private TrainingTypeRepository trainingTypeRepository;
    private TrainingServiceImpl service;

    @BeforeEach
    void setUp() {
        trainingRepository = mock(TrainingRepository.class);
        traineeRepository = mock(TraineeRepository.class);
        trainerRepository = mock(TrainerRepository.class);
        trainingTypeRepository = mock(TrainingTypeRepository.class);

        service = new TrainingServiceImpl(trainingRepository, traineeRepository, trainerRepository, trainingTypeRepository);
    }

    @Test
    void create_shouldSaveTrainingAndReturnDto() {
        Trainee trainee = new Trainee();
        trainee.setUsername("trainee1");
        trainee.setTrainers(new HashSet<>());

        Trainer trainer = new Trainer();
        trainer.setUsername("trainer1");
        trainer.setTrainees(new HashSet<>());

        TrainingType type = new TrainingType();
        type.setId(1L);
        type.setTrainingTypeName("Yoga");

        TrainingCreateDto dto = new TrainingCreateDto();
        dto.setTraineeUsername("trainee1");
        dto.setTrainerUsername("trainer1");
        dto.setTrainingTypeId(1L);
        dto.setTrainingName("Morning Yoga");
        dto.setTrainingDate(LocalDate.of(2026, 2, 28));
        dto.setTrainingDuration(60);

        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(type));

        TrainingResponseDto response = service.create(dto);

        assertNotNull(response);
        assertEquals("Morning Yoga", response.getTrainingName());
        assertEquals(LocalDate.of(2026, 2, 28), response.getTrainingDate());
        assertEquals(60, response.getTrainingDuration());

        assertTrue(trainer.getTrainees().contains(trainee));
        assertTrue(trainee.getTrainers().contains(trainer));

        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void create_shouldThrowIfTraineeNotFound() {
        TrainingCreateDto dto = new TrainingCreateDto();
        dto.setTraineeUsername("missing");
        dto.setTrainerUsername("trainer1");
        dto.setTrainingTypeId(1L);
        dto.setTrainingName("Yoga");
        dto.setTrainingDate(LocalDate.now());
        dto.setTrainingDuration(60);

        when(traineeRepository.findByUsername("missing")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.create(dto));
        assertEquals("Trainee not found", exception.getMessage());
    }

    @Test
    void create_shouldThrowIfTrainerNotFound() {
        Trainee trainee = new Trainee();
        trainee.setUsername("trainee1");
        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));

        TrainingCreateDto dto = new TrainingCreateDto();
        dto.setTraineeUsername("trainee1");
        dto.setTrainerUsername("missing");
        dto.setTrainingTypeId(1L);
        dto.setTrainingName("Yoga");
        dto.setTrainingDate(LocalDate.now());
        dto.setTrainingDuration(60);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.create(dto));
        assertEquals("Trainer not found", exception.getMessage());
    }

    @Test
    void create_shouldThrowIfTrainingTypeNotFound() {
        Trainee trainee = new Trainee();
        trainee.setUsername("trainee1");
        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));

        Trainer trainer = new Trainer();
        trainer.setUsername("trainer1");
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        TrainingCreateDto dto = new TrainingCreateDto();
        dto.setTraineeUsername("trainee1");
        dto.setTrainerUsername("trainer1");
        dto.setTrainingTypeId(999L);
        dto.setTrainingName("Yoga");
        dto.setTrainingDate(LocalDate.now());
        dto.setTrainingDuration(60);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.create(dto));
        assertEquals("Training type not found", exception.getMessage());
    }

    @Test
    void create_shouldCreateTrainingSuccessfully() {
        Trainee trainee = new Trainee();
        trainee.setUsername("trainee1");
        trainee.setTrainers(new HashSet<>());
        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));

        Trainer trainer = new Trainer();
        trainer.setUsername("trainer1");
        trainer.setTrainees(new HashSet<>());
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));

        TrainingCreateDto dto = new TrainingCreateDto();
        dto.setTraineeUsername("trainee1");
        dto.setTrainerUsername("trainer1");
        dto.setTrainingTypeId(1L);
        dto.setTrainingName("Yoga");
        dto.setTrainingDate(LocalDate.now());
        dto.setTrainingDuration(60);

        TrainingResponseDto response = service.create(dto);

        assertEquals("Yoga", response.getTrainingName());
        assertTrue(trainer.getTrainees().contains(trainee));
        assertTrue(trainee.getTrainers().contains(trainer));
    }

    @Test
    void create_shouldThrowIfTraineeUsernameNull() {
        TrainingCreateDto dto = new TrainingCreateDto();
        dto.setTrainerUsername("trainer1");
        dto.setTrainingTypeId(1L);
        dto.setTrainingName("Strength");
        dto.setTrainingDate(LocalDate.now());
        dto.setTrainingDuration(60);

        dto.setTraineeUsername(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.create(dto));

        assertEquals("Trainee username is required", ex.getMessage());
    }

    @Test
    void create_shouldThrowIfTrainerUsernameBlank() {
        TrainingCreateDto dto = new TrainingCreateDto();
        dto.setTraineeUsername("trainee1");
        dto.setTrainerUsername(" ");
        dto.setTrainingTypeId(1L);
        dto.setTrainingName("Strength");
        dto.setTrainingDate(LocalDate.now());
        dto.setTrainingDuration(60);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.create(dto));

        assertEquals("Trainer username is required", ex.getMessage());
    }

    @Test
    void create_shouldThrowIfTrainingTypeIdNull() {
        TrainingCreateDto dto = new TrainingCreateDto();
        dto.setTraineeUsername("trainee1");
        dto.setTrainerUsername("trainer1");
        dto.setTrainingName("Strength");
        dto.setTrainingDate(LocalDate.now());
        dto.setTrainingDuration(60);

        dto.setTrainingTypeId(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.create(dto));

        assertEquals("Training type is required", ex.getMessage());
    }

    @Test
    void create_shouldThrowIfTrainingNameBlank() {
        TrainingCreateDto dto = new TrainingCreateDto();
        dto.setTraineeUsername("trainee1");
        dto.setTrainerUsername("trainer1");
        dto.setTrainingTypeId(1L);
        dto.setTrainingName(" ");
        dto.setTrainingDate(LocalDate.now());
        dto.setTrainingDuration(60);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.create(dto));

        assertEquals("Training name is required", ex.getMessage());
    }

    @Test
    void create_shouldThrowIfTrainingDateNull() {
        TrainingCreateDto dto = new TrainingCreateDto();
        dto.setTraineeUsername("trainee1");
        dto.setTrainerUsername("trainer1");
        dto.setTrainingTypeId(1L);
        dto.setTrainingName("Strength");
        dto.setTrainingDuration(60);

        dto.setTrainingDate(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.create(dto));

        assertEquals("Training date is required", ex.getMessage());
    }

    @Test
    void create_shouldThrowIfTrainingDurationInvalid() {
        TrainingCreateDto dto = new TrainingCreateDto();
        dto.setTraineeUsername("trainee1");
        dto.setTrainerUsername("trainer1");
        dto.setTrainingTypeId(1L);
        dto.setTrainingName("Strength");
        dto.setTrainingDate(LocalDate.now());
        dto.setTrainingDuration(0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.create(dto));

        assertEquals("Training duration must be positive", ex.getMessage());
    }


    @Test
    void create_shouldThrowIfValidationFails() {
        TrainingCreateDto dto = new TrainingCreateDto();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.create(dto));
        assertEquals("Trainee username is required", exception.getMessage());
    }
}