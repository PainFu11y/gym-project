package com.gym_project.service.impl;

import com.gym_project.dto.create.TraineeCreateDto;
import com.gym_project.dto.filter.TraineeTrainingFilterDto;
import com.gym_project.dto.response.TraineeResponseDto;
import com.gym_project.dto.response.TrainerResponseDto;
import com.gym_project.dto.response.TrainingResponseDto;
import com.gym_project.dto.update.TraineeUpdateDto;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.Training;
import com.gym_project.entity.TrainingType;
import com.gym_project.repository.TraineeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceImplTest {

    private TraineeRepository traineeRepository;
    private TraineeServiceImpl traineeService;

    @BeforeEach
    void setUp() {
        traineeRepository = mock(TraineeRepository.class);
        traineeService = new TraineeServiceImpl(traineeRepository);
    }

    @Test
    void create_shouldGenerateUsernameAndSaveTrainee() {
        TraineeCreateDto dto = new TraineeCreateDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setDateOfBirth(LocalDate.of(2000, 1, 1));
        dto.setAddress("Yerevan");

        when(traineeRepository.findUsernamesStartingWith("John.Doe"))
                .thenReturn(List.of());

        TraineeResponseDto response = traineeService.create(dto);

        verify(traineeRepository).save(any(Trainee.class));

        assertNotNull(response.getUsername());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
    }

    @Test
    void create_shouldGenerateUniqueUsernameWhenExists() {
        TraineeCreateDto dto = new TraineeCreateDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setDateOfBirth(LocalDate.of(2000, 1, 1));
        dto.setAddress("Yerevan");

        when(traineeRepository.findUsernamesStartingWith("John.Doe"))
                .thenReturn(List.of("John.Doe", "John.Doe1"));

        TraineeResponseDto response = traineeService.create(dto);

        verify(traineeRepository).save(any(Trainee.class));

        assertNotNull(response.getUsername());
        assertTrue(response.getUsername().startsWith("John.Doe"));
    }

    @Test
    void create_shouldSetGeneratedPassword() {
        TraineeCreateDto dto = new TraineeCreateDto();
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setDateOfBirth(LocalDate.of(1995, 5, 5));
        dto.setAddress("Yerevan");

        when(traineeRepository.findUsernamesStartingWith("Jane.Smith"))
                .thenReturn(List.of());

        traineeService.create(dto);

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeRepository).save(captor.capture());

        Trainee saved = captor.getValue();

        assertNotNull(saved.getPassword());
        assertFalse(saved.getPassword().isBlank());
    }

    @Test
    void update_shouldChangeFields() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        trainee.setFirstName("Old");
        trainee.setLastName("Name");

        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        TraineeUpdateDto dto = new TraineeUpdateDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setAddress("Yerevan");
        dto.setDateOfBirth(LocalDate.of(2000, 1, 1));

        TraineeResponseDto response = traineeService.update("john.doe", dto);

        assertEquals("John", trainee.getFirstName());
        assertEquals("Doe", trainee.getLastName());
        assertEquals("Yerevan", trainee.getAddress());
        assertEquals(LocalDate.of(2000, 1, 1), trainee.getDateOfBirth());
    }

    @Test
    void deleteByUsername_shouldCallDelete() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        trainee.setTrainers(new HashSet<>());

        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        traineeService.deleteByUsername("john.doe");

        verify(traineeRepository).delete(trainee);
    }


    @Test
    void changePassword_shouldCallRepository() {
        String newPassword = "newPass123";
        traineeService.changePassword("john.doe", newPassword);
        verify(traineeRepository).changePassword("john.doe", newPassword);
    }

    @Test
    void changePassword_shouldThrowOnBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                traineeService.changePassword("john.doe", ""));
    }

    @Test
    void validateCredentials_shouldReturnDtoWhenCorrect() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        trainee.setPassword("pass");
        trainee.setActive(true);

        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        TraineeResponseDto dto = traineeService.validateCredentials("john.doe", "pass");

        assertEquals("john.doe", dto.getUsername());
    }

    @Test
    void validateCredentials_shouldThrowWhenPasswordIncorrect() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        trainee.setPassword("pass");
        trainee.setActive(true);

        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        assertThrows(RuntimeException.class, () ->
                traineeService.validateCredentials("john.doe", "wrong"));
    }

    @Test
    void create_shouldThrowWhenFieldsInvalid() {
        TraineeCreateDto dto = new TraineeCreateDto();
        assertThrows(IllegalArgumentException.class, () ->
                traineeService.create(dto));
    }

    @Test
    void update_shouldThrowWhenFieldsInvalid() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        TraineeUpdateDto dto = new TraineeUpdateDto();
        dto.setFirstName("");
        assertThrows(IllegalArgumentException.class, () ->
                traineeService.update("john.doe", dto));
    }

    @Test
    void getByUsername_shouldReturnDto() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        TraineeResponseDto dto = traineeService.getByUsername("john.doe");

        assertEquals("john.doe", dto.getUsername());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
    }

    @Test
    void getByUsername_shouldThrowIfNotFound() {
        when(traineeRepository.findByUsername("missing")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                traineeService.getByUsername("missing")
        );

        assertEquals("Trainee not found", ex.getMessage());
    }

    @Test
    void getAll_shouldReturnDtos() {
        Trainee t1 = new Trainee();
        t1.setUsername("u1");
        Trainee t2 = new Trainee();
        t2.setUsername("u2");

        when(traineeRepository.findAll()).thenReturn(List.of(t1, t2));

        List<TraineeResponseDto> dtos = traineeService.getAll();

        assertEquals(2, dtos.size());
        assertTrue(dtos.stream().anyMatch(d -> d.getUsername().equals("u1")));
        assertTrue(dtos.stream().anyMatch(d -> d.getUsername().equals("u2")));
    }

    @Test
    void getTrainings_shouldReturnMappedDtos() {
        TraineeTrainingFilterDto filter = new TraineeTrainingFilterDto();
        Training training = new Training();
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        training.setTrainee(trainee);
        training.setTrainer(new Trainer());
        training.setTrainingType(new TrainingType());
        training.setTrainingName("Yoga");
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60);

        when(traineeRepository.findTrainingsByTraineeAndFilter("john.doe", filter))
                .thenReturn(List.of(training));

        List<TrainingResponseDto> result = traineeService.getTrainings("john.doe", filter);

        assertEquals(1, result.size());
        assertEquals("john.doe", result.get(0).getTraineeUsername());
        assertEquals("Yoga", result.get(0).getTrainingName());
    }

    @Test
    void validate_shouldThrowIfFirstNameEmpty() {
        TraineeCreateDto dto = new TraineeCreateDto();
        dto.setFirstName("");
        dto.setLastName("Doe");
        dto.setDateOfBirth(LocalDate.of(2000, 1, 1));
        dto.setAddress("Yerevan");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                traineeService.create(dto)
        );
        assertEquals("First name cannot be empty", ex.getMessage());
    }

    @Test
    void validate_shouldThrowIfLastNameEmpty() {
        TraineeCreateDto dto = new TraineeCreateDto();
        dto.setFirstName("John");
        dto.setLastName("");
        dto.setDateOfBirth(LocalDate.of(2000, 1, 1));
        dto.setAddress("Yerevan");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                traineeService.create(dto)
        );
        assertEquals("Last name cannot be empty", ex.getMessage());
    }

    @Test
    void validate_shouldThrowIfDateOfBirthNull() {
        TraineeCreateDto dto = new TraineeCreateDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setAddress("Yerevan");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                traineeService.create(dto)
        );
        assertEquals("Date of birth cannot be null", ex.getMessage());
    }

    @Test
    void validate_shouldThrowIfDateOfBirthInFuture() {
        TraineeCreateDto dto = new TraineeCreateDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setDateOfBirth(LocalDate.now().plusDays(1));
        dto.setAddress("Yerevan");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                traineeService.create(dto)
        );
        assertEquals("Date of birth must be in the past", ex.getMessage());
    }

    @Test
    void validate_shouldThrowIfAddressEmpty() {
        TraineeCreateDto dto = new TraineeCreateDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setDateOfBirth(LocalDate.of(2000, 1, 1));
        dto.setAddress("");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                traineeService.create(dto)
        );
        assertEquals("Address cannot be empty", ex.getMessage());
    }

    @Test
    void toggleActiveStatus_shouldActivateInactiveTrainee() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        trainee.setActive(false);

        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        TraineeResponseDto response = traineeService.toggleActiveStatus("john.doe");

        assertTrue(response.isActive());
        assertTrue(trainee.isActive());
        assertEquals("john.doe", response.getUsername());
    }

    @Test
    void toggleActiveStatus_shouldDeactivateActiveTrainee() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        trainee.setActive(true);

        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        TraineeResponseDto response = traineeService.toggleActiveStatus("john.doe");

        assertFalse(response.isActive());
        assertFalse(trainee.isActive());
        assertEquals("john.doe", response.getUsername());
    }

    @Test
    void toggleActiveStatus_shouldThrowIfTraineeNotFound() {
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                traineeService.toggleActiveStatus("john.doe")
        );

        assertEquals("Trainee not found", ex.getMessage());
    }

    @Test
    void getTrainers_shouldReturnTrainerDtos() {
        String traineeUsername = "john.doe";

        Trainer trainer1 = new Trainer();
        trainer1.setUsername("trainer1");
        Trainer trainer2 = new Trainer();
        trainer2.setUsername("trainer2");

        when(traineeRepository.findTrainersByTraineeUsername(traineeUsername))
                .thenReturn(List.of(trainer1, trainer2));

        List<TrainerResponseDto> result = traineeService.getTrainers(traineeUsername);

        assertEquals(2, result.size());
        assertEquals("trainer1", result.get(0).getUsername());
        assertEquals("trainer2", result.get(1).getUsername());
    }

    @Test
    void getTrainers_shouldReturnEmptyListIfNoTrainers() {
        String traineeUsername = "john.doe";

        when(traineeRepository.findTrainersByTraineeUsername(traineeUsername))
                .thenReturn(List.of());

        List<TrainerResponseDto> result = traineeService.getTrainers(traineeUsername);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}