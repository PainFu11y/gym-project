package com.gym_project.repository.impl;

import com.gym_project.dto.filter.TraineeTrainingFilterDto;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.Training;
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

class TraineeRepositoryImplTest {

    private TraineeRepositoryImpl repository;
    private EntityManager entityManager;

    @BeforeEach
    void setUp() throws Exception {
        entityManager = mock(EntityManager.class);
        repository = new TraineeRepositoryImpl();

        Field emField = TraineeRepositoryImpl.class.getDeclaredField("entityManager");
        emField.setAccessible(true);
        emField.set(repository, entityManager);
    }

    @Test
    void save_shouldCallPersist() {
        Trainee trainee = new Trainee();
        repository.save(trainee);
        verify(entityManager).persist(trainee);
    }

    @Test
    void update_shouldCallMerge() {
        Trainee trainee = new Trainee();
        when(entityManager.merge(trainee)).thenReturn(trainee);
        Trainee result = repository.update(trainee);
        assertEquals(trainee, result);
        verify(entityManager).merge(trainee);
    }

    @Test
    void delete_shouldCallRemove() {
        Trainee trainee = mock(Trainee.class);
        when(entityManager.contains(trainee)).thenReturn(true);
        repository.delete(trainee);
        verify(entityManager).remove(trainee);
    }

    @Test
    void findById_shouldReturnOptional() {
        Trainee trainee = new Trainee();
        when(entityManager.find(Trainee.class, 1L)).thenReturn(trainee);

        Optional<Trainee> result = repository.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(trainee, result.get());
    }

    @Test
    void findAll_shouldCallQueryGetResultList() {
        TypedQuery<Trainee> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT t FROM Trainee t", Trainee.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Trainee()));

        List<Trainee> result = repository.findAll();
        assertEquals(1, result.size());
        verify(query).getResultList();
    }

    @Test
    void findByUsername_shouldReturnOptional() {
        TypedQuery<Trainee> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT t FROM Trainee t WHERE t.username = :username", Trainee.class))
                .thenReturn(query);
        when(query.setParameter("username", "john")).thenReturn(query);
        when(query.getResultStream()).thenReturn(List.of(new Trainee()).stream());

        Optional<Trainee> result = repository.findByUsername("john");
        assertTrue(result.isPresent());
    }

    @Test
    void existsByUsername_shouldReturnTrue() {
        TypedQuery<Long> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT COUNT(t) FROM Trainee t WHERE t.username = :username", Long.class))
                .thenReturn(query);
        when(query.setParameter("username", "john")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(1L);

        boolean exists = repository.existsByUsername("john");
        assertTrue(exists);
    }

    @Test
    void changePassword_shouldMergeUpdatedEntity() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john");

        TraineeRepositoryImpl spyRepo = spy(repository);
        doReturn(Optional.of(trainee)).when(spyRepo).findByUsername("john");

        spyRepo.changePassword("john", "newpass");
        assertEquals("newpass", trainee.getPassword());
        verify(entityManager).merge(trainee);
    }


    @Test
    void deleteByUsername_shouldCallRemove() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john");

        TraineeRepositoryImpl spyRepo = spy(repository);
        doReturn(Optional.of(trainee)).when(spyRepo).findByUsername("john");

        when(entityManager.contains(trainee)).thenReturn(false);
        when(entityManager.merge(trainee)).thenReturn(trainee);

        spyRepo.deleteByUsername("john");

        verify(entityManager).remove(trainee);
    }

    @Test
    void findTrainingsByTraineeAndFilter_shouldBuildQuery() {
        TypedQuery<Training> query = mock(TypedQuery.class);
        TraineeTrainingFilterDto filter = new TraineeTrainingFilterDto();
        filter.setFromDate(LocalDate.now());
        filter.setToDate(LocalDate.now().plusDays(1));

        when(entityManager.createQuery(anyString(), eq(Training.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        List<Training> result = repository.findTrainingsByTraineeAndFilter("john", filter);
        assertNotNull(result);
        verify(query).getResultList();
    }

    @Test
    void findTrainingsByTraineeAndFilter_shouldReturnList() {
        TraineeTrainingFilterDto filter = new TraineeTrainingFilterDto();
        filter.setFromDate(LocalDate.of(2026, 2, 1));
        filter.setToDate(LocalDate.of(2026, 2, 28));
        filter.setTrainerName("John");
        filter.setTrainingTypeName("Yoga");

        Training training = new Training();

        TypedQuery<Training> typedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Training.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), any())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(training));

        List<Training> result = repository.findTrainingsByTraineeAndFilter("trainee1", filter);

        assertEquals(1, result.size());
        assertSame(training, result.get(0));

        verify(typedQuery).setParameter("username", "trainee1");
        verify(typedQuery).setParameter("fromDate", filter.getFromDate());
        verify(typedQuery).setParameter("toDate", filter.getToDate());
        verify(typedQuery).setParameter("trainerName", "%John%");
        verify(typedQuery).setParameter("trainingTypeName", "Yoga");
    }

    @Test
    void findTrainingsByTraineeAndFilter_withoutOptionalFilters_shouldSetOnlyUsername() {
        TraineeTrainingFilterDto filter = new TraineeTrainingFilterDto();

        Training training = new Training();
        TypedQuery<Training> typedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Training.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), any())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(training));

        List<Training> result = repository.findTrainingsByTraineeAndFilter("trainee1", filter);

        assertEquals(1, result.size());
        assertSame(training, result.get(0));

        verify(typedQuery).setParameter("username", "trainee1");
        verify(typedQuery, never()).setParameter(eq("fromDate"), any());
        verify(typedQuery, never()).setParameter(eq("toDate"), any());
        verify(typedQuery, never()).setParameter(eq("trainerName"), any());
        verify(typedQuery, never()).setParameter(eq("trainingTypeName"), any());
    }

    @Test
    void findTrainersByTraineeUsername_shouldReturnTrainers() {
        String traineeUsername = "john.doe";

        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT tr FROM Trainee t JOIN t.trainers tr WHERE t.username = :username", Trainer.class))
                .thenReturn(query);
        when(query.setParameter("username", traineeUsername)).thenReturn(query);

        List<Trainer> expected = List.of(new Trainer(), new Trainer());
        when(query.getResultList()).thenReturn(expected);

        List<Trainer> actual = repository.findTrainersByTraineeUsername(traineeUsername);

        assertEquals(expected, actual);
        verify(entityManager).createQuery(
                "SELECT tr FROM Trainee t JOIN t.trainers tr WHERE t.username = :username", Trainer.class);
        verify(query).setParameter("username", traineeUsername);
        verify(query).getResultList();
    }

    @Test
    void findTrainersByTraineeUsername_shouldReturnEmptyListWhenNoTrainers() {
        String traineeUsername = "jane.doe";

        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT tr FROM Trainee t JOIN t.trainers tr WHERE t.username = :username", Trainer.class))
                .thenReturn(query);
        when(query.setParameter("username", traineeUsername)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        List<Trainer> actual = repository.findTrainersByTraineeUsername(traineeUsername);

        assertTrue(actual.isEmpty());
        verify(entityManager).createQuery(
                "SELECT tr FROM Trainee t JOIN t.trainers tr WHERE t.username = :username", Trainer.class);
        verify(query).setParameter("username", traineeUsername);
        verify(query).getResultList();
    }
}