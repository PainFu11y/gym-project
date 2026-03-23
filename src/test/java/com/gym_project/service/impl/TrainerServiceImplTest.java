package com.gym_project.service.impl;

import com.gym_project.dto.create.request.TrainerCreateRequestDto;
import com.gym_project.dto.create.response.TrainerCreateResponseDto;
import com.gym_project.dto.filter.TrainerTrainingFilterDto;
import com.gym_project.dto.response.TrainerResponseDto;
import com.gym_project.dto.response.TrainerSummaryDto;
import com.gym_project.dto.response.TrainingResponseDto;
import com.gym_project.dto.update.request.TrainerUpdateRequestDto;
import com.gym_project.dto.update.response.TrainerUpdateResponseDto;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.TrainingType;
import com.gym_project.exception.EntityNotFoundException;
import com.gym_project.mapper.TrainerMapper;
import com.gym_project.mapper.TrainingMapper;
import com.gym_project.repository.TrainerRepository;
import com.gym_project.repository.TrainingRepository;
import com.gym_project.repository.TrainingTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock private TrainerRepository trainerRepository;
    @Mock private TrainingRepository trainingRepository;
    @Mock private TrainingTypeRepository trainingTypeRepository;
    @Mock private TrainerMapper trainerMapper;
    @Mock private TrainingMapper trainingMapper;

    @InjectMocks
    private TrainerServiceImpl trainerService;

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
        trainer.setActive(true);
        trainer.setSpecialization(trainingType);
        trainer.setTrainees(new HashSet<>());
    }

    @Test
    void create_shouldGenerateUsernameAndSaveTrainer() {
        TrainerCreateRequestDto dto = new TrainerCreateRequestDto();
        dto.setFirstName("John");
        dto.setLastName("Smith");
        dto.setTrainingTypeId(1L);

        TrainerCreateResponseDto responseDto = new TrainerCreateResponseDto();
        responseDto.setUsername("John.Smith");

        when(trainerRepository.findUsernamesStartingWith("John.Smith")).thenReturn(List.of());
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainerMapper.toCreateResponseDto(any(Trainer.class))).thenReturn(responseDto);

        TrainerCreateResponseDto result = trainerService.create(dto);

        assertThat(result.getUsername()).isEqualTo("John.Smith");
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void create_shouldAppendSuffixWhenUsernameAlreadyExists() {
        TrainerCreateRequestDto dto = new TrainerCreateRequestDto();
        dto.setFirstName("John");
        dto.setLastName("Smith");
        dto.setTrainingTypeId(1L);

        TrainerCreateResponseDto responseDto = new TrainerCreateResponseDto();
        responseDto.setUsername("John.Smith1");

        when(trainerRepository.findUsernamesStartingWith("John.Smith")).thenReturn(List.of("John.Smith"));
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainerMapper.toCreateResponseDto(any(Trainer.class))).thenReturn(responseDto);

        TrainerCreateResponseDto result = trainerService.create(dto);

        assertThat(result.getUsername()).isEqualTo("John.Smith1");
    }

    @Test
    void create_shouldThrowEntityNotFoundException_whenTrainingTypeNotFound() {
        TrainerCreateRequestDto dto = new TrainerCreateRequestDto();
        dto.setFirstName("John");
        dto.setLastName("Smith");
        dto.setTrainingTypeId(99L);

        when(trainerRepository.findUsernamesStartingWith("John.Smith")).thenReturn(List.of());
        when(trainingTypeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.create(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(trainerRepository, never()).save(any());
    }


    @Test
    void getByUsername_shouldThrowEntityNotFoundException_whenNotFound() {
        when(trainerRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.getByUsername("unknown"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("unknown");
    }


    @Test
    void update_shouldUpdateAndReturnDto() {
        TrainerUpdateRequestDto dto = new TrainerUpdateRequestDto();
        dto.setUsername("John.Smith");
        dto.setFirstName("Johnny");
        dto.setLastName("Smith");

        TrainerUpdateResponseDto responseDto = new TrainerUpdateResponseDto();
        responseDto.setUsername("John.Smith");
        responseDto.setFirstName("Johnny");

        when(trainerRepository.findByUsername("John.Smith")).thenReturn(Optional.of(trainer));
        when(trainerMapper.toUpdateResponseDto(trainer)).thenReturn(responseDto);

        TrainerUpdateResponseDto result = trainerService.update(dto);

        assertThat(result.getUsername()).isEqualTo("John.Smith");
        assertThat(result.getFirstName()).isEqualTo("Johnny");
        verify(trainerRepository).update(trainer);
    }

    @Test
    void update_shouldThrowEntityNotFoundException_whenNotFound() {
        TrainerUpdateRequestDto dto = new TrainerUpdateRequestDto();
        dto.setUsername("ghost");

        when(trainerRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.update(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("ghost");

        verify(trainerRepository, never()).update(any());
    }

    @Test
    void getUnassignedActiveTrainers_shouldReturnMappedSummaries() {
        TrainerUpdateResponseDto updateDto = new TrainerUpdateResponseDto();
        updateDto.setUsername("John.Smith");
        updateDto.setFirstName("John");
        updateDto.setLastName("Smith");
        updateDto.setSpecialization("Yoga");

        when(trainerRepository.findUnassignedActiveTrainersByTraineeUsername("Jane.Doe"))
                .thenReturn(List.of(trainer));
        when(trainerMapper.toUpdateResponseDtoList(List.of(trainer))).thenReturn(List.of(updateDto));

        List<TrainerSummaryDto> result =
                trainerService.getUnassignedActiveTrainersByTraineeUsername("Jane.Doe");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("John.Smith");
        assertThat(result.get(0).getSpecialization()).isEqualTo("Yoga");
    }

    @Test
    void getUnassignedActiveTrainers_shouldReturnEmptyList_whenNoneFound() {
        when(trainerRepository.findUnassignedActiveTrainersByTraineeUsername("Jane.Doe"))
                .thenReturn(List.of());
        when(trainerMapper.toUpdateResponseDtoList(List.of())).thenReturn(List.of());

        List<TrainerSummaryDto> result =
                trainerService.getUnassignedActiveTrainersByTraineeUsername("Jane.Doe");

        assertThat(result).isEmpty();
    }


    @Test
    void getTrainerTrainingsByFilter_shouldReturnMappedTrainings() {
        TrainerTrainingFilterDto filter = new TrainerTrainingFilterDto();
        filter.setUsername("John.Smith");
        filter.setFromDate(LocalDate.now().minusDays(30));
        filter.setToDate(LocalDate.now());
        filter.setTraineeName("Jane");

        TrainingResponseDto trainingDto = new TrainingResponseDto();

        when(trainingRepository.findByTrainerFilter(any())).thenReturn(List.of());
        when(trainingMapper.toResponseDtoList(List.of())).thenReturn(List.of(trainingDto));

        List<TrainingResponseDto> result = trainerService.getTrainerTrainingsByFilter(filter);

        assertThat(result).hasSize(1);
        verify(trainingRepository).findByTrainerFilter(argThat(r ->
                r.getUsername().equals("John.Smith") &&
                        r.getTraineeName().equals("Jane")
        ));
    }

    @Test
    void getTrainerTrainingsByFilter_shouldReturnEmptyList_whenNoTrainingsFound() {
        TrainerTrainingFilterDto filter = new TrainerTrainingFilterDto();
        filter.setUsername("John.Smith");

        when(trainingRepository.findByTrainerFilter(any())).thenReturn(List.of());
        when(trainingMapper.toResponseDtoList(List.of())).thenReturn(List.of());

        List<TrainingResponseDto> result = trainerService.getTrainerTrainingsByFilter(filter);

        assertThat(result).isEmpty();
    }

    @Test
    void toggleStatus_shouldCallRepository() {
        doNothing().when(trainerRepository).toggleStatus("John.Smith");

        trainerService.toggleStatus("John.Smith");

        verify(trainerRepository).toggleStatus("John.Smith");
    }
}