package com.gym_project.service.impl;

import com.gym_project.dto.create.request.TraineeCreateRequestDto;
import com.gym_project.dto.create.response.TraineeCreateResponseDto;
import com.gym_project.dto.request.LoginRequestDto;
import com.gym_project.dto.request.TraineeTrainingsFilterRequestDto;
import com.gym_project.dto.response.TraineeResponseDto;
import com.gym_project.dto.response.TrainingResponseDto;
import com.gym_project.dto.update.request.TraineeUpdateRequestDto;
import com.gym_project.dto.update.request.UpdateTraineeTrainerListRequestDto;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.Training;
import com.gym_project.entity.TrainingType;
import com.gym_project.exception.EntityNotFoundException;
import com.gym_project.exception.InvalidCredentialsException;
import com.gym_project.mapper.TraineeMapper;
import com.gym_project.mapper.TrainerMapper;
import com.gym_project.mapper.TrainingMapper;
import com.gym_project.repository.TraineeRepository;
import com.gym_project.repository.TrainerRepository;
import com.gym_project.repository.TrainingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock private TraineeRepository traineeRepository;
    @Mock private TrainingRepository trainingRepository;
    @Mock private TraineeMapper traineeMapper;
    @Mock private TrainingMapper trainingMapper;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee trainee;
    private Trainer trainer;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Yoga");

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("John");
        trainer.setLastName("Smith");
        trainer.setUsername("John.Smith");
        trainer.setPassword("pass123");
        trainer.setSpecialization(trainingType);
        trainer.setTrainees(new HashSet<>());

        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("Jane");
        trainee.setLastName("Doe");
        trainee.setUsername("Jane.Doe");
        trainee.setPassword("pass456");
        trainee.setTrainers(new HashSet<>());
    }

    @Test
    void create_shouldGenerateUsernameAndSaveTrainee() {
        TraineeCreateRequestDto dto = new TraineeCreateRequestDto();
        dto.setFirstName("Jane");
        dto.setLastName("Doe");

        TraineeCreateResponseDto responseDto = new TraineeCreateResponseDto();
        responseDto.setUsername("Jane.Doe");

        when(traineeRepository.findUsernamesStartingWith("Jane.Doe")).thenReturn(List.of());
        when(traineeMapper.toEntity(dto)).thenReturn(trainee);
        when(traineeMapper.toCreateResponseDto(trainee)).thenReturn(responseDto);

        TraineeCreateResponseDto result = traineeService.create(dto);

        assertThat(result.getUsername()).isEqualTo("Jane.Doe");
        verify(traineeRepository).save(trainee);
    }

    @Test
    void create_shouldAppendSuffixWhenUsernameAlreadyExists() {
        TraineeCreateRequestDto dto = new TraineeCreateRequestDto();
        dto.setFirstName("Jane");
        dto.setLastName("Doe");

        TraineeCreateResponseDto responseDto = new TraineeCreateResponseDto();
        responseDto.setUsername("Jane.Doe1");

        when(traineeRepository.findUsernamesStartingWith("Jane.Doe")).thenReturn(List.of("Jane.Doe"));
        when(traineeMapper.toEntity(dto)).thenReturn(trainee);
        when(traineeMapper.toCreateResponseDto(trainee)).thenReturn(responseDto);

        TraineeCreateResponseDto result = traineeService.create(dto);

        assertThat(result.getUsername()).isEqualTo("Jane.Doe1");
        verify(traineeRepository).save(trainee);
    }

    @Test
    void getByUsername_shouldReturnTrainee_whenFound() {
        TraineeResponseDto responseDto = new TraineeResponseDto();
        responseDto.setUsername("Jane.Doe");

        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainee));
        when(traineeMapper.toResponseDto(trainee)).thenReturn(responseDto);

        TraineeResponseDto result = traineeService.getByUsername("Jane.Doe");

        assertThat(result.getUsername()).isEqualTo("Jane.Doe");
    }

    @Test
    void getByUsername_shouldThrowEntityNotFoundException_whenNotFound() {
        when(traineeRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.getByUsername("unknown"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("unknown");
    }

    @Test
    void update_shouldUpdateAndReturnTrainee() {
        TraineeUpdateRequestDto dto = new TraineeUpdateRequestDto();
        dto.setUsername("Jane.Doe");
        dto.setFirstName("Janet");
        dto.setLastName("Doe");

        TraineeResponseDto responseDto = new TraineeResponseDto();
        responseDto.setUsername("Jane.Doe");

        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainee));
        when(traineeRepository.update(trainee)).thenReturn(trainee);
        when(traineeMapper.toResponseDto(trainee)).thenReturn(responseDto);

        TraineeResponseDto result = traineeService.update(dto);

        assertThat(result.getUsername()).isEqualTo("Jane.Doe");
        verify(traineeMapper).updateEntity(dto, trainee);
        verify(traineeRepository).update(trainee);
    }

    @Test
    void update_shouldThrowEntityNotFoundException_whenTraineeNotFound() {
        TraineeUpdateRequestDto dto = new TraineeUpdateRequestDto();
        dto.setUsername("ghost");

        when(traineeRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.update(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("ghost");
    }


    @Test
    void deleteByUsername_shouldCallRepository() {
        doNothing().when(traineeRepository).deleteByUsername("Jane.Doe");

        traineeService.deleteByUsername("Jane.Doe");

        verify(traineeRepository).deleteByUsername("Jane.Doe");
    }


    @Test
    void toggleStatus_shouldCallRepository() {
        doNothing().when(traineeRepository).toggleStatus("Jane.Doe");

        traineeService.toggleStatus("Jane.Doe");

        verify(traineeRepository).toggleStatus("Jane.Doe");
    }


    @Test
    void getTraineeTrainings_shouldReturnMappedList() {
        TraineeTrainingsFilterRequestDto filter = new TraineeTrainingsFilterRequestDto();
        filter.setUsername("Jane.Doe");

        Training training = new Training();
        TrainingResponseDto trainingDto = new TrainingResponseDto();

        when(trainingRepository.findByTraineeFilter(filter)).thenReturn(List.of(training));
        when(trainingMapper.toResponseDtoList(List.of(training))).thenReturn(List.of(trainingDto));

        List<TrainingResponseDto> result = traineeService.getTraineeTrainings(filter);

        assertThat(result).hasSize(1);
    }

    @Test
    void getTraineeTrainings_shouldReturnEmptyList_whenNoTrainingsFound() {
        TraineeTrainingsFilterRequestDto filter = new TraineeTrainingsFilterRequestDto();
        filter.setUsername("Jane.Doe");

        when(trainingRepository.findByTraineeFilter(filter)).thenReturn(List.of());
        when(trainingMapper.toResponseDtoList(List.of())).thenReturn(List.of());

        List<TrainingResponseDto> result = traineeService.getTraineeTrainings(filter);

        assertThat(result).isEmpty();
    }


    @Test
    void validateCredentials_shouldReturnTrainee_whenCredentialsCorrect() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setUsername("Jane.Doe");
        dto.setPassword("pass456");

        TraineeResponseDto responseDto = new TraineeResponseDto();
        responseDto.setUsername("Jane.Doe");

        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainee));
        when(traineeMapper.toResponseDto(trainee)).thenReturn(responseDto);

        TraineeResponseDto result = traineeService.validateCredentials(dto);

        assertThat(result.getUsername()).isEqualTo("Jane.Doe");
    }

    @Test
    void validateCredentials_shouldThrowInvalidCredentialsException_whenUsernameNotFound() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setUsername("ghost");
        dto.setPassword("any");

        when(traineeRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.validateCredentials(dto))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void validateCredentials_shouldThrowInvalidCredentialsException_whenPasswordWrong() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setUsername("Jane.Doe");
        dto.setPassword("wrongPassword");

        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainee));

        assertThatThrownBy(() -> traineeService.validateCredentials(dto))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void updateTrainerList_shouldThrowEntityNotFoundException_whenTraineeNotFound() {
        UpdateTraineeTrainerListRequestDto dto = new UpdateTraineeTrainerListRequestDto();
        dto.setTraineeUsername("ghost");
        dto.setTrainers(List.of());

        when(traineeRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.updateTrainerList(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("ghost");
    }
}