package com.gym_project.repository.impl;

import com.gym_project.entity.Trainee;
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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeRepositoryImplTest {

    @Mock private EntityManager entityManager;

    @InjectMocks
    private TraineeRepositoryImpl traineeRepository;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUsername("Jane.Doe");
        trainee.setPassword("pass123");
        trainee.setActive(true);
    }


    @Test
    void save_shouldPersistTrainee() {
        traineeRepository.save(trainee);

        verify(entityManager).persist(trainee);
    }


    @Test
    void update_shouldMergeAndReturnTrainee() {
        when(entityManager.merge(trainee)).thenReturn(trainee);

        Trainee result = traineeRepository.update(trainee);

        assertThat(result).isEqualTo(trainee);
        verify(entityManager).merge(trainee);
    }


    @Test
    void delete_shouldRemoveTrainee_whenManagedByEntityManager() {
        when(entityManager.contains(trainee)).thenReturn(true);

        traineeRepository.delete(trainee);

        verify(entityManager).remove(trainee);
    }

    @Test
    void delete_shouldMergeThenRemove_whenTraineeIsDetached() {
        Trainee merged = new Trainee();
        when(entityManager.contains(trainee)).thenReturn(false);
        when(entityManager.merge(trainee)).thenReturn(merged);

        traineeRepository.delete(trainee);

        verify(entityManager).merge(trainee);
        verify(entityManager).remove(merged);
    }


    @Test
    void findAll_shouldReturnAllTrainees() {
        TypedQuery<Trainee> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT t FROM Trainee t", Trainee.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(trainee));

        List<Trainee> result = traineeRepository.findAll();

        assertThat(result).hasSize(1).contains(trainee);
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoTraineesExist() {
        TypedQuery<Trainee> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT t FROM Trainee t", Trainee.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        List<Trainee> result = traineeRepository.findAll();

        assertThat(result).isEmpty();
    }


    @Test
    void findByUsername_shouldReturnTrainee_whenFound() {
        TypedQuery<Trainee> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT t FROM Trainee t WHERE t.username = :username", Trainee.class))
                .thenReturn(query);
        when(query.setParameter("username", "Jane.Doe")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(trainee));

        Optional<Trainee> result = traineeRepository.findByUsername("Jane.Doe");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("Jane.Doe");
    }

    @Test
    void findByUsername_shouldReturnEmpty_whenNotFound() {
        TypedQuery<Trainee> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT t FROM Trainee t WHERE t.username = :username", Trainee.class))
                .thenReturn(query);
        when(query.setParameter("username", "ghost")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.empty());

        Optional<Trainee> result = traineeRepository.findByUsername("ghost");

        assertThat(result).isEmpty();
    }


    @Test
    void existsByUsername_shouldReturnTrue_whenTraineeExists() {
        TypedQuery<Long> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT COUNT(t) FROM Trainee t WHERE t.username = :username", Long.class))
                .thenReturn(query);
        when(query.setParameter("username", "Jane.Doe")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(1L);

        assertThat(traineeRepository.existsByUsername("Jane.Doe")).isTrue();
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenTraineeDoesNotExist() {
        TypedQuery<Long> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT COUNT(t) FROM Trainee t WHERE t.username = :username", Long.class))
                .thenReturn(query);
        when(query.setParameter("username", "ghost")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(0L);

        assertThat(traineeRepository.existsByUsername("ghost")).isFalse();
    }


    @Test
    void findUsernamesStartingWith_shouldReturnMatchingUsernames() {
        TypedQuery<String> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT u.username FROM User u WHERE u.username LIKE :prefix", String.class))
                .thenReturn(query);
        when(query.setParameter("prefix", "Jane.Doe%")).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of("Jane.Doe", "Jane.Doe1"));

        List<String> result = traineeRepository.findUsernamesStartingWith("Jane.Doe");

        assertThat(result).containsExactly("Jane.Doe", "Jane.Doe1");
    }

    @Test
    void findUsernamesStartingWith_shouldReturnEmptyList_whenNoMatch() {
        TypedQuery<String> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT u.username FROM User u WHERE u.username LIKE :prefix", String.class))
                .thenReturn(query);
        when(query.setParameter("prefix", "Unknown%")).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        List<String> result = traineeRepository.findUsernamesStartingWith("Unknown");

        assertThat(result).isEmpty();
    }


    @Test
    void changePassword_shouldUpdatePassword_whenTraineeFound() {
        TypedQuery<Trainee> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT t FROM Trainee t WHERE t.username = :username", Trainee.class))
                .thenReturn(query);
        when(query.setParameter("username", "Jane.Doe")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(trainee));
        when(entityManager.merge(trainee)).thenReturn(trainee);

        traineeRepository.changePassword("Jane.Doe", "newPass");

        assertThat(trainee.getPassword()).isEqualTo("newPass");
        verify(entityManager).merge(trainee);
    }

    @Test
    void changePassword_shouldThrowIllegalArgumentException_whenTraineeNotFound() {
        TypedQuery<Trainee> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT t FROM Trainee t WHERE t.username = :username", Trainee.class))
                .thenReturn(query);
        when(query.setParameter("username", "ghost")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.empty());

        assertThatThrownBy(() -> traineeRepository.changePassword("ghost", "newPass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ghost");

        verify(entityManager, never()).merge(any());
    }


    @Test
    void toggleStatus_shouldDeactivateTrainee_whenCurrentlyActive() {
        trainee.setActive(true);

        TypedQuery<Trainee> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT t FROM Trainee t WHERE t.username = :username", Trainee.class))
                .thenReturn(query);
        when(query.setParameter("username", "Jane.Doe")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(trainee);

        traineeRepository.toggleStatus("Jane.Doe");

        assertThat(trainee.isActive()).isFalse();
        verify(entityManager).merge(trainee);
    }

    @Test
    void toggleStatus_shouldActivateTrainee_whenCurrentlyInactive() {
        trainee.setActive(false);

        TypedQuery<Trainee> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT t FROM Trainee t WHERE t.username = :username", Trainee.class))
                .thenReturn(query);
        when(query.setParameter("username", "Jane.Doe")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(trainee);

        traineeRepository.toggleStatus("Jane.Doe");

        assertThat(trainee.isActive()).isTrue();
        verify(entityManager).merge(trainee);
    }


    @Test
    void deleteByUsername_shouldRemoveTrainee_whenFound() {
        TypedQuery<Trainee> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT t FROM Trainee t WHERE t.username = :username", Trainee.class))
                .thenReturn(query);
        when(query.setParameter("username", "Jane.Doe")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(trainee));
        when(entityManager.contains(trainee)).thenReturn(true);

        traineeRepository.deleteByUsername("Jane.Doe");

        verify(entityManager).remove(trainee);
    }

    @Test
    void deleteByUsername_shouldDoNothing_whenTraineeNotFound() {
        TypedQuery<Trainee> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT t FROM Trainee t WHERE t.username = :username", Trainee.class))
                .thenReturn(query);
        when(query.setParameter("username", "ghost")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.empty());

        traineeRepository.deleteByUsername("ghost");

        verify(entityManager, never()).remove(any());
    }
}