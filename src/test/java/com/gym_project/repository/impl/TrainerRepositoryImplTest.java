package com.gym_project.repository.impl;

import com.gym_project.dto.filter.TrainerTrainingFilterDto;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerRepositoryImplTest {

    private EntityManager entityManager;
    private TrainerRepositoryImpl repository;

    @BeforeEach
    void setUp() throws Exception {
        entityManager = mock(EntityManager.class);
        repository = new TrainerRepositoryImpl();

        var field = TrainerRepositoryImpl.class.getDeclaredField("entityManager");
        field.setAccessible(true);
        field.set(repository, entityManager);
    }

    @Test
    void save_shouldCallPersist() {
        Trainer trainer = new Trainer();
        repository.save(trainer);
        verify(entityManager).persist(trainer);
    }

    @Test
    void update_shouldCallMerge() {
        Trainer trainer = new Trainer();
        when(entityManager.merge(trainer)).thenReturn(trainer);
        Trainer updated = repository.update(trainer);
        assertEquals(trainer, updated);
        verify(entityManager).merge(trainer);
    }

    @Test
    void delete_shouldCallRemoveWithMergeWhenNotContained() {
        Trainer trainer = new Trainer();
        when(entityManager.contains(trainer)).thenReturn(false);
        when(entityManager.merge(trainer)).thenReturn(trainer);

        repository.delete(trainer);

        verify(entityManager).merge(trainer);
        verify(entityManager).remove(trainer);
    }

    @Test
    void delete_shouldCallRemoveDirectlyWhenContained() {
        Trainer trainer = new Trainer();
        when(entityManager.contains(trainer)).thenReturn(true);

        repository.delete(trainer);

        verify(entityManager, never()).merge(trainer);
        verify(entityManager).remove(trainer);
    }

    @Test
    void findById_shouldReturnOptional() {
        Trainer trainer = new Trainer();
        when(entityManager.find(Trainer.class, 1L)).thenReturn(trainer);

        Optional<Trainer> result = repository.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(trainer, result.get());
    }

    @Test
    void findAll_shouldReturnList() {
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT t FROM Trainer t", Trainer.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Trainer()));

        List<Trainer> result = repository.findAll();
        assertEquals(1, result.size());
        verify(query).getResultList();
    }

    @Test
    void findBySpecialization_shouldReturnList() {
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT t FROM Trainer t WHERE t.specialization = :spec", Trainer.class))
                .thenReturn(query);
        when(query.setParameter("spec", "Yoga")).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Trainer()));

        List<Trainer> result = repository.findBySpecialization("Yoga");
        assertEquals(1, result.size());
        verify(query).getResultList();
    }

    @Test
    void changePassword_shouldCallMerge() {
        Trainer trainer = new Trainer();
        trainer.setUsername("john");

        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT t FROM Trainer t WHERE t.username = :username", Trainer.class))
                .thenReturn(query);
        when(query.setParameter("username", "john")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(trainer));

        repository.changePassword("john", "newpass");

        assertEquals("newpass", trainer.getPassword());
        verify(entityManager).merge(trainer);
    }




    @Test
    void findUsernamesStartingWith_shouldReturnList() {
        TypedQuery<String> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT t.username FROM Trainer t WHERE t.username LIKE :pattern", String.class))
                .thenReturn(query);
        when(query.setParameter("pattern", "jo%")).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of("john"));

        List<String> result = repository.findUsernamesStartingWith("jo");
        assertEquals(1, result.size());
    }

    @Test
    void findTrainingsByTrainerAndFilter_shouldReturnList() {
        TypedQuery<Training> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Training.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Training()));

        TrainerTrainingFilterDto filter = new TrainerTrainingFilterDto();
        filter.setFromDate(LocalDate.now());
        filter.setToDate(LocalDate.now());

        List<Training> result = repository.findTrainingsByTrainerAndFilter("john", filter);
        assertEquals(1, result.size());
    }

    @Test
    void findUnassignedTrainersByTraineeUsername_shouldReturnEmptyListIfNoTrainers() {
        TypedQuery<Trainee> traineeQuery = mock(TypedQuery.class);
        TypedQuery<Trainer> trainerQuery = mock(TypedQuery.class);
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");

        when(entityManager.createQuery(
                "SELECT t FROM Trainee t WHERE t.username = :username", Trainee.class))
                .thenReturn(traineeQuery);
        when(traineeQuery.setParameter("username", "john.doe")).thenReturn(traineeQuery);
        when(traineeQuery.getSingleResult()).thenReturn(trainee);

        when(entityManager.createQuery(
                "SELECT tr FROM Trainer tr WHERE :trainee NOT MEMBER OF tr.trainees", Trainer.class))
                .thenReturn(trainerQuery);
        when(trainerQuery.setParameter("trainee", trainee)).thenReturn(trainerQuery);
        when(trainerQuery.getResultList()).thenReturn(List.of());

        List<Trainer> result = repository.findUnassignedTrainersByTraineeUsername("john.doe");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(entityManager).createQuery("SELECT t FROM Trainee t WHERE t.username = :username", Trainee.class);
        verify(entityManager).createQuery("SELECT tr FROM Trainer tr WHERE :trainee NOT MEMBER OF tr.trainees", Trainer.class);
    }

    @Test
    void findUnassignedTrainersByTraineeUsername_shouldReturnListOfTrainers() {
        TypedQuery<Trainee> traineeQuery = mock(TypedQuery.class);
        TypedQuery<Trainer> trainerQuery = mock(TypedQuery.class);
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");

        Trainer t1 = new Trainer();
        t1.setUsername("trainer1");
        Trainer t2 = new Trainer();
        t2.setUsername("trainer2");

        when(entityManager.createQuery(
                "SELECT t FROM Trainee t WHERE t.username = :username", Trainee.class))
                .thenReturn(traineeQuery);
        when(traineeQuery.setParameter("username", "john.doe")).thenReturn(traineeQuery);
        when(traineeQuery.getSingleResult()).thenReturn(trainee);

        when(entityManager.createQuery(
                "SELECT tr FROM Trainer tr WHERE :trainee NOT MEMBER OF tr.trainees", Trainer.class))
                .thenReturn(trainerQuery);
        when(trainerQuery.setParameter("trainee", trainee)).thenReturn(trainerQuery);
        when(trainerQuery.getResultList()).thenReturn(List.of(t1, t2));

        List<Trainer> result = repository.findUnassignedTrainersByTraineeUsername("john.doe");

        assertEquals(2, result.size());
        assertEquals("trainer1", result.get(0).getUsername());
        assertEquals("trainer2", result.get(1).getUsername());
    }

    @Test
    void findByUsernameAndPassword_shouldReturnEmptyOptionalIfNotFound() {
        TypedQuery<Trainer> query = mock(TypedQuery.class);

        when(entityManager.createQuery(
                "SELECT t FROM Trainer t WHERE t.username = :username AND t.password = :password", Trainer.class))
                .thenReturn(query);
        when(query.setParameter("username", "john.doe")).thenReturn(query);
        when(query.setParameter("password", "pass123")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.empty());

        Optional<Trainer> result = repository.findByUsernameAndPassword("john.doe", "pass123");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(entityManager).createQuery(
                "SELECT t FROM Trainer t WHERE t.username = :username AND t.password = :password", Trainer.class);
    }

    @Test
    void findByUsernameAndPassword_shouldReturnTrainerIfFound() {
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        Trainer trainer = new Trainer();
        trainer.setUsername("john.doe");
        trainer.setPassword("pass123");

        when(entityManager.createQuery(
                "SELECT t FROM Trainer t WHERE t.username = :username AND t.password = :password", Trainer.class))
                .thenReturn(query);
        when(query.setParameter("username", "john.doe")).thenReturn(query);
        when(query.setParameter("password", "pass123")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(trainer));

        Optional<Trainer> result = repository.findByUsernameAndPassword("john.doe", "pass123");

        assertTrue(result.isPresent());
        assertEquals("john.doe", result.get().getUsername());
        assertEquals("pass123", result.get().getPassword());
    }

    @Test
    void findTraineesByTrainerUsername_shouldReturnEmptyListIfNoTrainees() {
        TypedQuery<Trainee> query = mock(TypedQuery.class);

        when(entityManager.createQuery(
                "SELECT trn FROM Trainer t JOIN t.trainees trn WHERE t.username = :username", Trainee.class))
                .thenReturn(query);
        when(query.setParameter("username", "john.doe")).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        List<Trainee> result = repository.findTraineesByTrainerUsername("john.doe");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(entityManager).createQuery(
                "SELECT trn FROM Trainer t JOIN t.trainees trn WHERE t.username = :username", Trainee.class);
    }

    @Test
    void findTraineesByTrainerUsername_shouldReturnListOfTrainees() {
        TypedQuery<Trainee> query = mock(TypedQuery.class);
        Trainee t1 = new Trainee();
        t1.setUsername("trainee1");
        Trainee t2 = new Trainee();
        t2.setUsername("trainee2");

        when(entityManager.createQuery(
                "SELECT trn FROM Trainer t JOIN t.trainees trn WHERE t.username = :username", Trainee.class))
                .thenReturn(query);
        when(query.setParameter("username", "john.doe")).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(t1, t2));

        List<Trainee> result = repository.findTraineesByTrainerUsername("john.doe");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("trainee1", result.get(0).getUsername());
        assertEquals("trainee2", result.get(1).getUsername());
    }
    @Test
    void deleteByUsername_shouldRemoveTrainerIfExists() {
        Trainer trainer = new Trainer();
        trainer.setUsername("john.doe");

        TrainerRepositoryImpl spyRepo = spy(repository);
        doReturn(Optional.of(trainer)).when(spyRepo).findByUsername("john.doe");

        when(entityManager.contains(trainer)).thenReturn(true);

        spyRepo.deleteByUsername("john.doe");

        verify(entityManager).remove(trainer);
    }

    @Test
    void deleteByUsername_shouldMergeAndRemoveIfNotContained() {
        Trainer trainer = new Trainer();
        trainer.setUsername("john.doe");

        TrainerRepositoryImpl spyRepo = spy(repository);
        doReturn(Optional.of(trainer)).when(spyRepo).findByUsername("john.doe");

        when(entityManager.contains(trainer)).thenReturn(false);
        Trainer mergedTrainer = new Trainer();
        when(entityManager.merge(trainer)).thenReturn(mergedTrainer);

        spyRepo.deleteByUsername("john.doe");

        verify(entityManager).merge(trainer);
        verify(entityManager).remove(mergedTrainer);
    }

    @Test
    void deleteByUsername_shouldDoNothingIfTrainerNotFound() {
        TrainerRepositoryImpl spyRepo = spy(repository);
        doReturn(Optional.empty()).when(spyRepo).findByUsername("john.doe");

        spyRepo.deleteByUsername("john.doe");

        verify(entityManager, never()).remove(any());
    }

    @Test
    void findTrainingsByTrainerAndFilter_noFilters() {
        TrainerTrainingFilterDto filter = new TrainerTrainingFilterDto();
        String trainerUsername = "trainer1";

        TypedQuery<Training> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT tr FROM Training tr WHERE tr.trainer.username = :username", Training.class))
                .thenReturn(query);
        when(query.setParameter("username", trainerUsername)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Training(), new Training()));

        List<Training> result = repository.findTrainingsByTrainerAndFilter(trainerUsername, filter);

        assertEquals(2, result.size());
        verify(query).setParameter("username", trainerUsername);
        verify(query).getResultList();
    }

    @Test
    void findTrainingsByTrainerAndFilter_withDates() {
        TrainerTrainingFilterDto filter = new TrainerTrainingFilterDto();
        filter.setFromDate(LocalDate.of(2026, 1, 1));
        filter.setToDate(LocalDate.of(2026, 1, 31));
        String trainerUsername = "trainer1";

        TypedQuery<Training> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT tr FROM Training tr WHERE tr.trainer.username = :username AND tr.trainingDate >= :fromDate AND tr.trainingDate <= :toDate",
                Training.class))
                .thenReturn(query);
        when(query.setParameter("username", trainerUsername)).thenReturn(query);
        when(query.setParameter("fromDate", filter.getFromDate())).thenReturn(query);
        when(query.setParameter("toDate", filter.getToDate())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Training()));

        List<Training> result = repository.findTrainingsByTrainerAndFilter(trainerUsername, filter);

        assertEquals(1, result.size());
        verify(query).setParameter("username", trainerUsername);
        verify(query).setParameter("fromDate", filter.getFromDate());
        verify(query).setParameter("toDate", filter.getToDate());
        verify(query).getResultList();
    }

    @Test
    void findTrainingsByTrainerAndFilter_withTraineeName() {
        TrainerTrainingFilterDto filter = new TrainerTrainingFilterDto();
        filter.setTraineeName("John");
        String trainerUsername = "trainer1";

        String expectedQuery = "SELECT tr FROM Training tr WHERE tr.trainer.username = :username " +
                "AND (tr.trainee.firstName LIKE :traineeName OR tr.trainee.lastName LIKE :traineeName)";

        TypedQuery<Training> query = mock(TypedQuery.class);
        when(entityManager.createQuery(expectedQuery, Training.class)).thenReturn(query);
        when(query.setParameter("username", trainerUsername)).thenReturn(query);
        when(query.setParameter("traineeName", "%John%")).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Training()));

        List<Training> result = repository.findTrainingsByTrainerAndFilter(trainerUsername, filter);

        assertEquals(1, result.size());
        verify(query).setParameter("username", trainerUsername);
        verify(query).setParameter("traineeName", "%John%");
        verify(query).getResultList();
    }
}