package com.gym_project.service.impl;

import com.gym_project.dto.response.TrainingTypeResponseDto;
import com.gym_project.entity.TrainingType;
import com.gym_project.repository.TrainingTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceImplTest {

    @Mock private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingTypeServiceImpl trainingTypeService;

    @Test
    void findAll_shouldReturnMappedDtoList() {
        TrainingType yoga = new TrainingType();
        yoga.setId(1L);
        yoga.setTrainingTypeName("Yoga");

        TrainingType pilates = new TrainingType();
        pilates.setId(2L);
        pilates.setTrainingTypeName("Pilates");

        when(trainingTypeRepository.findAll()).thenReturn(List.of(yoga, pilates));

        List<TrainingTypeResponseDto> result = trainingTypeService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getTrainingType()).isEqualTo("Yoga");
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getTrainingType()).isEqualTo("Pilates");
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoTrainingTypesExist() {
        when(trainingTypeRepository.findAll()).thenReturn(List.of());

        List<TrainingTypeResponseDto> result = trainingTypeService.findAll();

        assertThat(result).isEmpty();
        verify(trainingTypeRepository).findAll();
    }

    @Test
    void findAll_shouldCallRepositoryExactlyOnce() {
        when(trainingTypeRepository.findAll()).thenReturn(List.of());

        trainingTypeService.findAll();

        verify(trainingTypeRepository, times(1)).findAll();
    }

    @Test
    void findAll_shouldCorrectlyMapIdAndName() {
        TrainingType entity = new TrainingType();
        entity.setId(42L);
        entity.setTrainingTypeName("CrossFit");

        when(trainingTypeRepository.findAll()).thenReturn(List.of(entity));

        TrainingTypeResponseDto result = trainingTypeService.findAll().get(0);

        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getTrainingType()).isEqualTo("CrossFit");
    }
}