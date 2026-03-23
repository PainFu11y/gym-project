package com.gym_project.repository.impl;

import com.gym_project.entity.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingTypeRepositoryImplTest {

    @Mock private EntityManager entityManager;

    @InjectMocks
    private TrainingTypeRepositoryImpl trainingTypeRepository;

    private TrainingType yoga;
    private TrainingType pilates;

    @BeforeEach
    void setUp() {
        yoga = new TrainingType();
        yoga.setId(1L);
        yoga.setTrainingTypeName("Yoga");

        pilates = new TrainingType();
        pilates.setId(2L);
        pilates.setTrainingTypeName("Pilates");
    }


    @Test
    void findById_shouldReturnTrainingType_whenFound() {
        when(entityManager.find(TrainingType.class, 1L)).thenReturn(yoga);

        Optional<TrainingType> result = trainingTypeRepository.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getTrainingTypeName()).isEqualTo("Yoga");
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        when(entityManager.find(TrainingType.class, 99L)).thenReturn(null);

        Optional<TrainingType> result = trainingTypeRepository.findById(99L);

        assertThat(result).isEmpty();
    }


    @Test
    void findAll_shouldReturnAllTrainingTypes() {
        TypedQuery<TrainingType> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT t FROM TrainingType t", TrainingType.class))
                .thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(yoga, pilates));

        List<TrainingType> result = trainingTypeRepository.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(TrainingType::getTrainingTypeName)
                .containsExactly("Yoga", "Pilates");
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoTrainingTypesExist() {
        TypedQuery<TrainingType> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT t FROM TrainingType t", TrainingType.class))
                .thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        List<TrainingType> result = trainingTypeRepository.findAll();

        assertThat(result).isEmpty();
    }
}