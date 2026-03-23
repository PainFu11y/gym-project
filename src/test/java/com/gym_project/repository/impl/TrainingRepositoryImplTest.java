package com.gym_project.repository.impl;

import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.Training;
import com.gym_project.entity.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingRepositoryImplTest {

    private TrainingRepositoryImpl repository;
    private EntityManager entityManager;

    @BeforeEach
    void setUp() throws Exception {
        entityManager = mock(EntityManager.class);
        repository = new TrainingRepositoryImpl();

        Field emField = TrainingRepositoryImpl.class.getDeclaredField("entityManager");
        emField.setAccessible(true);
        emField.set(repository, entityManager);
    }

    @Test
    void save_shouldCallPersist() {
        Training training = new Training();
        repository.save(training);
        verify(entityManager).persist(training);
    }

    @Test
    void update_shouldCallMerge() {
        Training training = new Training();
        when(entityManager.merge(training)).thenReturn(training);
        Training updated = repository.update(training);
        assertEquals(training, updated);
        verify(entityManager).merge(training);
    }

    @Test
    void delete_shouldCallRemove() {
        Training training = mock(Training.class);
        when(entityManager.contains(training)).thenReturn(true);
        repository.delete(training);
        verify(entityManager).remove(training);
    }

    @Test
    void findById_shouldReturnOptional() {
        Training training = new Training();
        when(entityManager.find(Training.class, 1L)).thenReturn(training);

        Optional<Training> result = repository.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(training, result.get());
    }

    @Test
    void findAll_shouldCallQueryGetResultList() {
        TypedQuery<Training> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT t FROM Training t", Training.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Training()));

        List<Training> trainings = repository.findAll();
        assertEquals(1, trainings.size());
        verify(query).getResultList();
    }

    @Test
    void findByTrainee_shouldSetParameter() {
        TypedQuery<Training> query = mock(TypedQuery.class);
        Trainee trainee = new Trainee();

        when(entityManager.createQuery("SELECT t FROM Training t WHERE t.trainee = :trainee", Training.class))
                .thenReturn(query);
        when(query.setParameter("trainee", trainee)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        List<Training> result = repository.findByTrainee(trainee);
        assertNotNull(result);
        verify(query).setParameter("trainee", trainee);
        verify(query).getResultList();
    }

    @Test
    void findByTrainer_shouldSetParameter() {
        TypedQuery<Training> query = mock(TypedQuery.class);
        Trainer trainer = new Trainer();

        when(entityManager.createQuery("SELECT t FROM Training t WHERE t.trainer = :trainer", Training.class))
                .thenReturn(query);
        when(query.setParameter("trainer", trainer)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        List<Training> result = repository.findByTrainer(trainer);
        assertNotNull(result);
        verify(query).setParameter("trainer", trainer);
        verify(query).getResultList();
    }

    @Test
    void findByTrainingType_shouldSetParameter() {
        TypedQuery<Training> query = mock(TypedQuery.class);
        TrainingType type = new TrainingType();

        when(entityManager.createQuery("SELECT t FROM Training t WHERE t.trainingType = :type", Training.class))
                .thenReturn(query);
        when(query.setParameter("type", type)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        List<Training> result = repository.findByTrainingType(type);
        assertNotNull(result);
        verify(query).setParameter("type", type);
        verify(query).getResultList();
    }

    @Test
    void findByDate_shouldSetParameter() {
        TypedQuery<Training> query = mock(TypedQuery.class);
        LocalDate date = LocalDate.now();

        when(entityManager.createQuery("SELECT t FROM Training t WHERE t.trainingDate = :date", Training.class))
                .thenReturn(query);
        when(query.setParameter("date", date)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        List<Training> result = repository.findByDate(date);
        assertNotNull(result);
        verify(query).setParameter("date", date);
        verify(query).getResultList();
    }

    @Test
    void findByDateRange_shouldSetParameters() {
        TypedQuery<Training> query = mock(TypedQuery.class);
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(1);

        when(entityManager.createQuery(
                "SELECT t FROM Training t WHERE t.trainingDate BETWEEN :start AND :end", Training.class))
                .thenReturn(query);
        when(query.setParameter("start", start)).thenReturn(query);
        when(query.setParameter("end", end)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        List<Training> result = repository.findByDateRange(start, end);
        assertNotNull(result);
        verify(query).setParameter("start", start);
        verify(query).setParameter("end", end);
        verify(query).getResultList();
    }

    @Test
    void findById_shouldReturnEmptyOptionalWhenNotFound() {
        when(entityManager.find(Training.class, 999L)).thenReturn(null);
        Optional<Training> result = repository.findById(999L);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoResults() {
        TypedQuery<Training> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT t FROM Training t", Training.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        List<Training> trainings = repository.findAll();
        assertNotNull(trainings);
        assertTrue(trainings.isEmpty());
    }

    @Test
    void delete_shouldCallMergeIfNotManaged() {
        Training training = mock(Training.class);
        when(entityManager.contains(training)).thenReturn(false);
        Training mergedTraining = mock(Training.class);
        when(entityManager.merge(training)).thenReturn(mergedTraining);

        repository.delete(training);

        verify(entityManager).merge(training);
        verify(entityManager).remove(mergedTraining);
    }

    @Test
    void save_shouldCallPersist_multipleTrainings() {
        Training t1 = new Training();
        Training t2 = new Training();

        repository.save(t1);
        repository.save(t2);

        verify(entityManager).persist(t1);
        verify(entityManager).persist(t2);
    }

    @Test
    void update_shouldReturnMergedTraining() {
        Training original = new Training();
        Training merged = new Training();
        when(entityManager.merge(original)).thenReturn(merged);

        Training result = repository.update(original);

        assertEquals(merged, result);
        verify(entityManager).merge(original);
    }
}