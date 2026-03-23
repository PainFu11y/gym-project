package com.gym_project.service.impl;

import com.gym_project.dto.create.TrainerCreateDto;
import com.gym_project.dto.response.TraineeResponseDto;
import com.gym_project.dto.update.TrainerUpdateDto;
import com.gym_project.dto.update.TraineeTrainersUpdateDto;
import com.gym_project.dto.filter.TrainerTrainingFilterDto;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.Training;
import com.gym_project.entity.TrainingType;
import com.gym_project.repository.TrainerRepository;
import com.gym_project.repository.TraineeRepository;
import com.gym_project.dto.response.TrainerResponseDto;
import com.gym_project.dto.response.TrainingResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplTest {

    private TrainerRepository trainerRepository;
    private TraineeRepository traineeRepository;
    private TrainerServiceImpl service;

    @BeforeEach
    void setUp() {
        trainerRepository = mock(TrainerRepository.class);
        traineeRepository = mock(TraineeRepository.class);
        service = new TrainerServiceImpl(trainerRepository, traineeRepository);
    }

    @Test
    void create_shouldGenerateUsernameAndPasswordAndSave() {
        TrainerCreateDto dto = new TrainerCreateDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setSpecialization("Yoga");

        when(trainerRepository.findUsernamesStartingWith("John.Doe")).thenReturn(List.of());

        TrainerResponseDto response = service.create(dto);

        assertNotNull(response.getUsername());
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void getByUsername_shouldReturnTrainer() {
        Trainer trainer = new Trainer();
        trainer.setUsername("john");
        when(trainerRepository.findByUsername("john")).thenReturn(Optional.of(trainer));

        TrainerResponseDto dto = service.getByUsername("john");

        assertEquals("john", dto.getUsername());
    }

    @Test
    void update_shouldCallMapperAndReturnDto() {
        Trainer trainer = new Trainer();
        trainer.setUsername("john");
        when(trainerRepository.findByUsername("john")).thenReturn(Optional.of(trainer));

        TrainerUpdateDto dto = new TrainerUpdateDto();
        dto.setSpecialization("Pilates");

        TrainerResponseDto updated = service.update("john", dto);

        assertEquals("john", updated.getUsername());
    }

    @Test
    void changePassword_shouldCallRepository() {
        service.changePassword("john", "newpass");
        verify(trainerRepository).changePassword("john", "newpass");
    }


    @Test
    void validateCredentials_shouldThrowIfPasswordIncorrect() {
        Trainer trainer = new Trainer();
        trainer.setUsername("john");
        trainer.setActive(true);
        trainer.setPassword("pass");
        when(trainerRepository.findByUsername("john")).thenReturn(Optional.of(trainer));

        Exception ex = assertThrows(RuntimeException.class, () -> service.validateCredentials("john", "wrong"));
        assertEquals("Invalid password", ex.getMessage());
    }

    @Test
    void updateTraineeTrainers_shouldAssignNewTrainers() {
        Trainee trainee = new Trainee();
        trainee.setUsername("trainee1");
        trainee.setTrainers(new java.util.HashSet<>());

        Trainer trainer1 = new Trainer();
        trainer1.setUsername("t1");
        trainer1.setTrainees(new java.util.HashSet<>());

        Trainer trainer2 = new Trainer();
        trainer2.setUsername("t2");
        trainer2.setTrainees(new java.util.HashSet<>());

        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("t1")).thenReturn(Optional.of(trainer1));
        when(trainerRepository.findByUsername("t2")).thenReturn(Optional.of(trainer2));

        TraineeTrainersUpdateDto dto = new TraineeTrainersUpdateDto();
        dto.setTrainerUsernames(List.of("t1", "t2"));

        List<TrainerResponseDto> result = service.updateTraineeTrainers("trainee1", dto);

        assertEquals(2, result.size());
        assertTrue(trainee.getTrainers().contains(trainer1));
        assertTrue(trainee.getTrainers().contains(trainer2));
        assertTrue(trainer1.getTrainees().contains(trainee));
        assertTrue(trainer2.getTrainees().contains(trainee));
    }

    @Test
    void getUnassignedTrainersByTraineeUsername_shouldThrowIfBlank() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> service.getUnassignedTrainersByTraineeUsername("  "));
        assertEquals("Trainee username must not be blank", ex.getMessage());
    }

    @Test
    void getUnassignedTrainersByTraineeUsername_shouldReturnMappedDtos() {
        Trainer trainer1 = new Trainer();
        trainer1.setUsername("trainer1");
        trainer1.setFirstName("John");
        trainer1.setLastName("Smith");

        Trainer trainer2 = new Trainer();
        trainer2.setUsername("trainer2");
        trainer2.setFirstName("Mike");
        trainer2.setLastName("Brown");

        when(trainerRepository.findUnassignedTrainersByTraineeUsername("trainee1"))
                .thenReturn(List.of(trainer1, trainer2));

        List<TrainerResponseDto> result =
                service.getUnassignedTrainersByTraineeUsername("trainee1");

        assertEquals(2, result.size());
        assertEquals("trainer1", result.get(0).getUsername());
        assertEquals("trainer2", result.get(1).getUsername());
    }


    @Test
    void getTrainings_shouldReturnMappedDtos() {
        TrainerTrainingFilterDto filter = new TrainerTrainingFilterDto();

        Training training = new Training();

        Trainee trainee = new Trainee();
        trainee.setUsername("trainee1");
        training.setTrainee(trainee);

        Trainer trainer = new Trainer();
        trainer.setUsername("john");
        training.setTrainer(trainer);

        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Yoga");
        training.setTrainingType(type);

        when(trainerRepository.findTrainingsByTrainerAndFilter("john", filter))
                .thenReturn(List.of(training));

        List<TrainingResponseDto> trainings = service.getTrainings("john", filter);

        assertEquals(1, trainings.size());
        assertEquals("trainee1", trainings.get(0).getTraineeUsername());
        assertEquals("Yoga", trainings.get(0).getTrainingTypeName());
    }

    @Test
    void validateUpdate_shouldThrowIfFirstNameBlank() {
        TrainerUpdateDto dto = new TrainerUpdateDto();
        dto.setFirstName("");
        dto.setLastName("Doe");
        dto.setSpecialization("Strength");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.update("john.doe", dto)
        );
        assertEquals("First name cannot be blank", ex.getMessage());
    }

    @Test
    void validateUpdate_shouldThrowIfLastNameBlank() {
        TrainerUpdateDto dto = new TrainerUpdateDto();
        dto.setFirstName("John");
        dto.setLastName("");
        dto.setSpecialization("Strength");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.update("john.doe", dto)
        );
        assertEquals("Last name cannot be blank", ex.getMessage());
    }

    @Test
    void validateUpdate_shouldThrowIfSpecializationBlank() {
        TrainerUpdateDto dto = new TrainerUpdateDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setSpecialization("");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.update("john.doe", dto)
        );
        assertEquals("Specialization cannot be blank", ex.getMessage());
    }

    @Test
    void deleteByUsername_shouldCallRepositoryDelete() {
        Trainer trainer = new Trainer();
        trainer.setUsername("john.doe");

        when(trainerRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainer));

        service.deleteByUsername("john.doe");

        verify(trainerRepository).deleteByUsername("john.doe");
    }

    @Test
    void deleteByUsername_shouldThrowIfTrainerNotFound() {
        when(trainerRepository.findByUsername("missing")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.deleteByUsername("missing")
        );

        assertEquals("Trainer not found", ex.getMessage());
    }

    @Test
    void getAll_shouldReturnMappedDtos() {
        Trainer trainer1 = new Trainer();
        trainer1.setUsername("t1");
        Trainer trainer2 = new Trainer();
        trainer2.setUsername("t2");

        when(trainerRepository.findAll()).thenReturn(List.of(trainer1, trainer2));

        List<TrainerResponseDto> result = service.getAll();

        assertEquals(2, result.size());
        assertEquals("t1", result.get(0).getUsername());
        assertEquals("t2", result.get(1).getUsername());
    }

    @Test
    void create_shouldThrowIfFirstNameNull() {
        TrainerCreateDto dto = new TrainerCreateDto();
        dto.setLastName("Doe");
        dto.setSpecialization("Strength");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.create(dto)
        );

        assertEquals("First name cannot be blank", ex.getMessage());
    }

    @Test
    void create_shouldThrowIfFirstNameBlank() {
        TrainerCreateDto dto = new TrainerCreateDto();
        dto.setFirstName(" ");
        dto.setLastName("Doe");
        dto.setSpecialization("Strength");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.create(dto)
        );

        assertEquals("First name cannot be blank", ex.getMessage());
    }

    @Test
    void create_shouldThrowIfLastNameNull() {
        TrainerCreateDto dto = new TrainerCreateDto();
        dto.setFirstName("John");
        dto.setSpecialization("Strength");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.create(dto)
        );

        assertEquals("Last name cannot be blank", ex.getMessage());
    }

    @Test
    void create_shouldThrowIfLastNameBlank() {
        TrainerCreateDto dto = new TrainerCreateDto();
        dto.setFirstName("John");
        dto.setLastName(" ");
        dto.setSpecialization("Strength");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.create(dto)
        );

        assertEquals("Last name cannot be blank", ex.getMessage());
    }

    @Test
    void create_shouldThrowIfSpecializationNull() {
        TrainerCreateDto dto = new TrainerCreateDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.create(dto)
        );

        assertEquals("Specialization cannot be blank", ex.getMessage());
    }

    @Test
    void create_shouldThrowIfSpecializationBlank() {
        TrainerCreateDto dto = new TrainerCreateDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setSpecialization(" ");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.create(dto)
        );

        assertEquals("Specialization cannot be blank", ex.getMessage());
    }

    @Test
    void toggleActiveStatus_shouldActivateInactiveTrainer() {
        Trainer trainer = new Trainer();
        trainer.setUsername("john.doe");
        trainer.setActive(false);

        when(trainerRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainer));

        TrainerResponseDto result = service.toggleActiveStatus("john.doe");

        assertTrue(trainer.isActive());
        assertTrue(result.isActive());
        verify(trainerRepository).findByUsername("john.doe");
    }

    @Test
    void toggleActiveStatus_shouldDeactivateActiveTrainer() {
        Trainer trainer = new Trainer();
        trainer.setUsername("jane.doe");
        trainer.setActive(true);

        when(trainerRepository.findByUsername("jane.doe")).thenReturn(Optional.of(trainer));

        TrainerResponseDto result = service.toggleActiveStatus("jane.doe");

        assertFalse(trainer.isActive());
        assertFalse(result.isActive());
        verify(trainerRepository).findByUsername("jane.doe");
    }

    @Test
    void toggleActiveStatus_shouldThrowIfTrainerNotFound() {
        when(trainerRepository.findByUsername("non.existent")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.toggleActiveStatus("non.existent")
        );

        assertEquals("Trainee not found", ex.getMessage());
        verify(trainerRepository).findByUsername("non.existent");
    }

    @Test
    void getTrainees_shouldReturnEmptyListIfNoTrainees() {
        when(trainerRepository.findTraineesByTrainerUsername("john.doe"))
                .thenReturn(List.of());

        List<TraineeResponseDto> result = service.getTrainees("john.doe");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(trainerRepository).findTraineesByTrainerUsername("john.doe");
    }

    @Test
    void getTrainees_shouldReturnMappedTrainees() {
        Trainee t1 = new Trainee();
        t1.setUsername("trainee1");
        Trainee t2 = new Trainee();
        t2.setUsername("trainee2");

        when(trainerRepository.findTraineesByTrainerUsername("john.doe"))
                .thenReturn(List.of(t1, t2));

        List<TraineeResponseDto> result = service.getTrainees("john.doe");

        assertEquals(2, result.size());
        assertEquals("trainee1", result.get(0).getUsername());
        assertEquals("trainee2", result.get(1).getUsername());

        verify(trainerRepository).findTraineesByTrainerUsername("john.doe");
    }
}