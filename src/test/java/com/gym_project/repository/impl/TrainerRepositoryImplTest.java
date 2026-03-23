package com.gym_project.repository.impl;

import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerRepositoryImplTest {

    @Mock private EntityManager entityManager;

    @InjectMocks
    private TrainerRepositoryImpl trainerRepository;

    private Trainer trainer;
    private Trainee trainee;

    @BeforeEach
    void setUp() {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Yoga");

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUsername("John.Smith");
        trainer.setPassword("pass123");
        trainer.setActive(true);
        trainer.setSpecialization(trainingType);
        trainer.setTrainees(new HashSet<>());

        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUsername("Jane.Doe");
        trainee.setPassword("pass456");
    }


    @Test
    void save_shouldPersistTrainer() {
        trainerRepository.save(trainer);

        verify(entityManager).persist(trainer);
    }


    @Test
    void update_shouldMergeAndReturnTrainer() {
        when(entityManager.merge(trainer)).thenReturn(trainer);

        Trainer result = trainerRepository.update(trainer);

        assertThat(result).isEqualTo(trainer);
        verify(entityManager).merge(trainer);
    }


    @Test
    void delete_shouldRemoveTrainer_whenManagedByEntityManager() {
        when(entityManager.contains(trainer)).thenReturn(true);

        trainerRepository.delete(trainer);

        verify(entityManager).remove(trainer);
    }

    @Test
    void delete_shouldMergeThenRemove_whenTrainerIsDetached() {
        Trainer merged = new Trainer();
        when(entityManager.contains(trainer)).thenReturn(false);
        when(entityManager.merge(trainer)).thenReturn(merged);

        trainerRepository.delete(trainer);

        verify(entityManager).merge(trainer);
        verify(entityManager).remove(merged);
    }


    @Test
    void findAll_shouldReturnAllTrainers() {
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT t FROM Trainer t", Trainer.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(trainer));

        List<Trainer> result = trainerRepository.findAll();

        assertThat(result).hasSize(1).contains(trainer);
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoTrainersExist() {
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT t FROM Trainer t", Trainer.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        List<Trainer> result = trainerRepository.findAll();

        assertThat(result).isEmpty();
    }


    @Test
    void findByUsernameAndPassword_shouldReturnTrainer_whenCredentialsMatch() {
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT t FROM Trainer t WHERE t.username = :username AND t.password = :password",
                Trainer.class)).thenReturn(query);
        when(query.setParameter("username", "John.Smith")).thenReturn(query);
        when(query.setParameter("password", "pass123")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(trainer));

        Optional<Trainer> result = trainerRepository.findByUsernameAndPassword("John.Smith", "pass123");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("John.Smith");
    }

    @Test
    void findByUsernameAndPassword_shouldReturnEmpty_whenCredentialsDontMatch() {
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT t FROM Trainer t WHERE t.username = :username AND t.password = :password",
                Trainer.class)).thenReturn(query);
        when(query.setParameter("username", "John.Smith")).thenReturn(query);
        when(query.setParameter("password", "wrong")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.empty());

        Optional<Trainer> result = trainerRepository.findByUsernameAndPassword("John.Smith", "wrong");

        assertThat(result).isEmpty();
    }


    @Test
    void findByUsername_shouldReturnTrainer_whenFound() {
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT t FROM Trainer t WHERE t.username = :username", Trainer.class))
                .thenReturn(query);
        when(query.setParameter("username", "John.Smith")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(trainer));

        Optional<Trainer> result = trainerRepository.findByUsername("John.Smith");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("John.Smith");
    }

    @Test
    void findByUsername_shouldReturnEmpty_whenNotFound() {
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT t FROM Trainer t WHERE t.username = :username", Trainer.class))
                .thenReturn(query);
        when(query.setParameter("username", "ghost")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.empty());

        Optional<Trainer> result = trainerRepository.findByUsername("ghost");

        assertThat(result).isEmpty();
    }


    @Test
    void changePassword_shouldUpdatePassword_whenTrainerFound() {
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT t FROM Trainer t WHERE t.username = :username", Trainer.class))
                .thenReturn(query);
        when(query.setParameter("username", "John.Smith")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(trainer));
        when(entityManager.merge(trainer)).thenReturn(trainer);

        trainerRepository.changePassword("John.Smith", "newPass");

        assertThat(trainer.getPassword()).isEqualTo("newPass");
        verify(entityManager).merge(trainer);
    }

    @Test
    void changePassword_shouldThrowIllegalArgumentException_whenTrainerNotFound() {
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT t FROM Trainer t WHERE t.username = :username", Trainer.class))
                .thenReturn(query);
        when(query.setParameter("username", "ghost")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.empty());

        assertThatThrownBy(() -> trainerRepository.changePassword("ghost", "newPass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ghost");

        verify(entityManager, never()).merge(any());
    }


    @Test
    void deleteByUsername_shouldRemoveTrainer_whenFound() {
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT t FROM Trainer t WHERE t.username = :username", Trainer.class))
                .thenReturn(query);
        when(query.setParameter("username", "John.Smith")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(trainer));
        when(entityManager.contains(trainer)).thenReturn(true);

        trainerRepository.deleteByUsername("John.Smith");

        verify(entityManager).remove(trainer);
    }

    @Test
    void deleteByUsername_shouldDoNothing_whenTrainerNotFound() {
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT t FROM Trainer t WHERE t.username = :username", Trainer.class))
                .thenReturn(query);
        when(query.setParameter("username", "ghost")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.empty());

        trainerRepository.deleteByUsername("ghost");

        verify(entityManager, never()).remove(any());
    }


    @Test
    void findUnassignedActiveTrainers_shouldReturnTrainers() {
        String jpql = "SELECT tr FROM Trainer tr " +
                "WHERE tr.isActive = true " +
                "AND NOT EXISTS (" +
                "   SELECT t FROM Trainee t JOIN t.trainers trn " +
                "   WHERE t.username = :username AND trn = tr" +
                ")";

        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery(jpql, Trainer.class)).thenReturn(query);
        when(query.setParameter("username", "Jane.Doe")).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(trainer));

        List<Trainer> result = trainerRepository.findUnassignedActiveTrainersByTraineeUsername("Jane.Doe");

        assertThat(result).hasSize(1).contains(trainer);
    }

    @Test
    void findUnassignedActiveTrainers_shouldReturnEmptyList_whenAllAssigned() {
        String jpql = "SELECT tr FROM Trainer tr " +
                "WHERE tr.isActive = true " +
                "AND NOT EXISTS (" +
                "   SELECT t FROM Trainee t JOIN t.trainers trn " +
                "   WHERE t.username = :username AND trn = tr" +
                ")";

        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery(jpql, Trainer.class)).thenReturn(query);
        when(query.setParameter("username", "Jane.Doe")).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        List<Trainer> result = trainerRepository.findUnassignedActiveTrainersByTraineeUsername("Jane.Doe");

        assertThat(result).isEmpty();
    }


    @Test
    void findUsernamesStartingWith_shouldReturnMatchingUsernames() {
        TypedQuery<String> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT u.username FROM User u WHERE u.username LIKE :prefix", String.class))
                .thenReturn(query);
        when(query.setParameter("prefix", "John.Smith%")).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of("John.Smith", "John.Smith1"));

        List<String> result = trainerRepository.findUsernamesStartingWith("John.Smith");

        assertThat(result).containsExactly("John.Smith", "John.Smith1");
    }

    @Test
    void findUsernamesStartingWith_shouldReturnEmptyList_whenNoMatch() {
        TypedQuery<String> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT u.username FROM User u WHERE u.username LIKE :prefix", String.class))
                .thenReturn(query);
        when(query.setParameter("prefix", "Unknown%")).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        List<String> result = trainerRepository.findUsernamesStartingWith("Unknown");

        assertThat(result).isEmpty();
    }


    @Test
    void findTraineesByTrainerUsername_shouldReturnTrainees() {
        TypedQuery<Trainee> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT trn FROM Trainer t JOIN t.trainees trn WHERE t.username = :username",
                Trainee.class)).thenReturn(query);
        when(query.setParameter("username", "John.Smith")).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(trainee));

        List<Trainee> result = trainerRepository.findTraineesByTrainerUsername("John.Smith");

        assertThat(result).hasSize(1).contains(trainee);
    }

    @Test
    void findTraineesByTrainerUsername_shouldReturnEmptyList_whenNoTrainees() {
        TypedQuery<Trainee> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT trn FROM Trainer t JOIN t.trainees trn WHERE t.username = :username",
                Trainee.class)).thenReturn(query);
        when(query.setParameter("username", "John.Smith")).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        List<Trainee> result = trainerRepository.findTraineesByTrainerUsername("John.Smith");

        assertThat(result).isEmpty();
    }


    @Test
    void findTrainersByUsernames_shouldReturnMatchingTrainers() {
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT tr FROM Trainer tr WHERE tr.username IN :usernames", Trainer.class))
                .thenReturn(query);
        when(query.setParameter("usernames", List.of("John.Smith"))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(trainer));

        List<Trainer> result = trainerRepository.findTrainersByUsernames(List.of("John.Smith"));

        assertThat(result).hasSize(1).contains(trainer);
    }

    @Test
    void findTrainersByUsernames_shouldReturnEmptyList_whenNoneMatch() {
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT tr FROM Trainer tr WHERE tr.username IN :usernames", Trainer.class))
                .thenReturn(query);
        when(query.setParameter("usernames", List.of("ghost"))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        List<Trainer> result = trainerRepository.findTrainersByUsernames(List.of("ghost"));

        assertThat(result).isEmpty();
    }


    @Test
    void toggleStatus_shouldDeactivateTrainer_whenCurrentlyActive() {
        trainer.setActive(true);

        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT t FROM Trainer t WHERE t.username = :username", Trainer.class))
                .thenReturn(query);
        when(query.setParameter("username", "John.Smith")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(trainer);

        trainerRepository.toggleStatus("John.Smith");

        assertThat(trainer.isActive()).isFalse();
        verify(entityManager).merge(trainer);
    }

    @Test
    void toggleStatus_shouldActivateTrainer_whenCurrentlyInactive() {
        trainer.setActive(false);

        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT t FROM Trainer t WHERE t.username = :username", Trainer.class))
                .thenReturn(query);
        when(query.setParameter("username", "John.Smith")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(trainer);

        trainerRepository.toggleStatus("John.Smith");

        assertThat(trainer.isActive()).isTrue();
        verify(entityManager).merge(trainer);
    }
}