package com.gym_project.repository.impl;

import com.gym_project.entity.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingTypeRepositoryImplTest {

    private EntityManager entityManager;
    private TrainingTypeRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        repository = new TrainingTypeRepositoryImpl();

        try {
            var field = TrainingTypeRepositoryImpl.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(repository, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void save_shouldCallPersist() {
        TrainingType type = new TrainingType();
        repository.save(type);
        verify(entityManager).persist(type);
    }

    @Test
    void update_shouldCallMergeAndReturnMerged() {
        TrainingType type = new TrainingType();
        when(entityManager.merge(type)).thenReturn(type);
        TrainingType result = repository.update(type);
        assertEquals(type, result);
        verify(entityManager).merge(type);
    }

    @Test
    void delete_shouldCallRemoveWithMergeWhenNotContained() {
        TrainingType type = new TrainingType();

        when(entityManager.contains(type)).thenReturn(false);
        when(entityManager.merge(type)).thenReturn(type);

        repository.delete(type);

        verify(entityManager).merge(type);
        verify(entityManager).remove(type);
    }

    @Test
    void delete_shouldCallRemoveDirectlyWhenContained() {
        TrainingType type = new TrainingType();

        when(entityManager.contains(type)).thenReturn(true);

        repository.delete(type);

        verify(entityManager, never()).merge(type);
        verify(entityManager).remove(type);
    }

    @Test
    void findById_shouldReturnOptional() {
        TrainingType type = new TrainingType();
        when(entityManager.find(TrainingType.class, 1L)).thenReturn(type);

        Optional<TrainingType> result = repository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(type, result.get());
    }

    @Test
    void findAll_shouldReturnList() {
        List<TrainingType> list = List.of(new TrainingType(), new TrainingType());
        when(entityManager.createQuery("SELECT t FROM TrainingType t", TrainingType.class))
                .thenReturn(mock(javax.persistence.TypedQuery.class));
        javax.persistence.TypedQuery<TrainingType> query = entityManager.createQuery("SELECT t FROM TrainingType t", TrainingType.class);
        when(query.getResultList()).thenReturn(list);

        List<TrainingType> result = repository.findAll();

        assertEquals(list, result);
    }

    @Test
    void findByName_shouldReturnOptional() {
        TrainingType type = new TrainingType();
        javax.persistence.TypedQuery<TrainingType> query = mock(javax.persistence.TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT t FROM TrainingType t WHERE t.trainingTypeName = :name", TrainingType.class))
                .thenReturn(query);
        when(query.setParameter("name", "Yoga")).thenReturn(query);
        when(query.getResultStream()).thenReturn(List.of(type).stream());

        Optional<TrainingType> result = repository.findByName("Yoga");

        assertTrue(result.isPresent());
        assertEquals(type, result.get());
    }

    @Test
    void findById_shouldReturnEmptyOptionalWhenNotFound() {
        when(entityManager.find(TrainingType.class, 999L)).thenReturn(null);
        Optional<TrainingType> result = repository.findById(999L);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoResults() {
        javax.persistence.TypedQuery<TrainingType> query = mock(javax.persistence.TypedQuery.class);
        when(entityManager.createQuery("SELECT t FROM TrainingType t", TrainingType.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        List<TrainingType> result = repository.findAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByName_shouldReturnEmptyOptionalWhenNoMatch() {
        javax.persistence.TypedQuery<TrainingType> query = mock(javax.persistence.TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT t FROM TrainingType t WHERE t.trainingTypeName = :name", TrainingType.class))
                .thenReturn(query);
        when(query.setParameter("name", "Pilates")).thenReturn(query);
        when(query.getResultStream()).thenReturn(List.<TrainingType>of().stream());

        Optional<TrainingType> result = repository.findByName("Pilates");
        assertTrue(result.isEmpty());
    }

    @Test
    void save_shouldBeCalledMultipleTimes() {
        TrainingType t1 = new TrainingType();
        TrainingType t2 = new TrainingType();

        repository.save(t1);
        repository.save(t2);

        verify(entityManager).persist(t1);
        verify(entityManager).persist(t2);
    }

    @Test
    void update_shouldReturnMergedInstance() {
        TrainingType original = new TrainingType();
        TrainingType merged = new TrainingType();
        when(entityManager.merge(original)).thenReturn(merged);

        TrainingType result = repository.update(original);

        assertEquals(merged, result);
        verify(entityManager).merge(original);
    }
}