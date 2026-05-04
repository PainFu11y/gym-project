package com.gym_project.service.impl;

import com.gym_project.actuator.metrics.GymMetrics;
import com.gym_project.client.ReportServiceClient;
import com.gym_project.client.ReportWorkloadRequest;
import com.gym_project.dto.create.request.TrainingCreateRequestDto;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.Training;
import com.gym_project.entity.TrainingType;
import com.gym_project.exception.EntityNotFoundException;
import com.gym_project.exception.ForbiddenOperationException;
import com.gym_project.repository.TraineeRepository;
import com.gym_project.repository.TrainerRepository;
import com.gym_project.repository.TrainingRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock private TrainingRepository  trainingRepository;
    @Mock private TrainerRepository   trainerRepository;
    @Mock private TraineeRepository   traineeRepository;
    @Mock private ReportServiceClient reportServiceClient;
    @Mock private GymMetrics          gymMetrics;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Trainee  trainee;
    private Trainer  trainer;
    private Training training;
    private TrainingCreateRequestDto dto;

    @BeforeEach
    void setUp() {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Yoga");

        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUsername("Jane.Doe");
        trainee.setPassword("pass456");

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("John");
        trainer.setLastName("Smith");
        trainer.setUsername("John.Smith");
        trainer.setPassword("pass123");
        trainer.setActive(true);
        trainer.setSpecialization(trainingType);
        trainer.setTrainees(new HashSet<>());

        training = new Training();
        training.setId(10L);
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingDate(LocalDate.of(2025, 4, 15));
        training.setTrainingDuration(60);
        training.setTrainingType(trainingType);
        training.setTrainingName("Morning Yoga");

        dto = new TrainingCreateRequestDto();
        dto.setTraineeUsername("Jane.Doe");
        dto.setTrainerUsername("John.Smith");
        dto.setTrainingName("Morning Yoga");
        dto.setTrainingDate(LocalDate.of(2025, 4, 15));
        dto.setTrainingDuration(60);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setAuthenticatedUser(String username) {
        var auth = new UsernamePasswordAuthenticationToken(
                username, null,
                List.of(new SimpleGrantedAuthority("ROLE_TRAINER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }


    @Test
    void create_shouldSaveTrainingAndLinkTraineeToTrainer() {
        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("John.Smith")).thenReturn(Optional.of(trainer));

        trainingService.create(dto);

        verify(trainingRepository).save(argThat(t ->
                t.getTrainingName().equals("Morning Yoga") &&
                t.getTrainee().equals(trainee) &&
                t.getTrainer().equals(trainer) &&
                t.getTrainingDuration().equals(60) &&
                t.getTrainingType().equals(trainer.getSpecialization())
        ));
        assertThat(trainer.getTrainees()).contains(trainee);
    }

    @Test
    void create_shouldSetTrainingTypeFromTrainerSpecialization() {
        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("John.Smith")).thenReturn(Optional.of(trainer));

        trainingService.create(dto);

        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);
        verify(trainingRepository).save(captor.capture());

        assertThat(captor.getValue().getTrainingType())
                .isEqualTo(trainer.getSpecialization());
        assertThat(captor.getValue().getTrainingType().getTrainingTypeName())
                .isEqualTo("Yoga");
    }

    @Test
    void create_shouldCallReportServiceWithAddAction() {
        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("John.Smith")).thenReturn(Optional.of(trainer));

        trainingService.create(dto);

        ArgumentCaptor<ReportWorkloadRequest> captor =
                ArgumentCaptor.forClass(ReportWorkloadRequest.class);
        verify(reportServiceClient).sendWorkload(captor.capture(), any());

        ReportWorkloadRequest sent = captor.getValue();
        assertThat(sent.getActionType()).isEqualTo(ReportWorkloadRequest.ActionType.ADD);
        assertThat(sent.getTrainerUsername()).isEqualTo("John.Smith");
        assertThat(sent.getTrainingDate()).isEqualTo(LocalDate.of(2025, 4, 15));
        assertThat(sent.getTrainingDuration()).isEqualTo(60);
    }

    @Test
    void create_shouldNotCallReportService_whenTraineeNotFound() {
        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.create(dto))
                .isInstanceOf(EntityNotFoundException.class);

        verifyNoInteractions(reportServiceClient);
    }

    @Test
    void create_shouldNotCallReportService_whenTrainerNotFound() {
        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("John.Smith")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.create(dto))
                .isInstanceOf(EntityNotFoundException.class);

        verifyNoInteractions(reportServiceClient);
    }

    @Test
    void create_shouldThrowEntityNotFoundException_whenTraineeNotFound() {
        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.create(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Jane.Doe");

        verify(trainerRepository, never()).findByUsername(any());
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowEntityNotFoundException_whenTrainerNotFound() {
        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("John.Smith")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.create(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("John.Smith");

        verify(trainingRepository, never()).save(any());
    }

    @Test
    void create_shouldNotLinkTraineeToTrainer_whenSaveFails() {
        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("John.Smith")).thenReturn(Optional.of(trainer));
        doThrow(new RuntimeException("DB error")).when(trainingRepository).save(any());

        assertThatThrownBy(() -> trainingService.create(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DB error");

        assertThat(trainer.getTrainees()).doesNotContain(trainee);
    }


    @Test
    void delete_shouldDeleteTrainingAndCallReportServiceWithDeleteAction() {
        setAuthenticatedUser("John.Smith");
        when(trainingRepository.findById(10L)).thenReturn(Optional.of(training));

        trainingService.delete(10L);

        verify(trainingRepository).delete(10L);

        ArgumentCaptor<ReportWorkloadRequest> captor =
                ArgumentCaptor.forClass(ReportWorkloadRequest.class);
        verify(reportServiceClient).sendWorkload(captor.capture(), any());

        ReportWorkloadRequest sent = captor.getValue();
        assertThat(sent.getActionType()).isEqualTo(ReportWorkloadRequest.ActionType.DELETE);
        assertThat(sent.getTrainerUsername()).isEqualTo("John.Smith");
        assertThat(sent.getTrainingDate()).isEqualTo(LocalDate.of(2025, 4, 15));
        assertThat(sent.getTrainingDuration()).isEqualTo(60);
    }

    @Test
    void delete_shouldThrowEntityNotFoundException_whenTrainingNotFound() {
        setAuthenticatedUser("John.Smith");
        when(trainingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.delete(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");

        verify(trainingRepository, never()).delete(any());
        verifyNoInteractions(reportServiceClient);
    }

    @Test
    void delete_shouldThrowForbiddenOperationException_whenCurrentUserIsNotOwner() {
        setAuthenticatedUser("someone.else");
        when(trainingRepository.findById(10L)).thenReturn(Optional.of(training));

        assertThatThrownBy(() -> trainingService.delete(10L))
                .isInstanceOf(ForbiddenOperationException.class);

        verify(trainingRepository, never()).delete(any());
        verifyNoInteractions(reportServiceClient);
    }

    @Test
    void delete_shouldNotCallReportService_whenTrainingNotFound() {
        setAuthenticatedUser("John.Smith");
        when(trainingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.delete(999L))
                .isInstanceOf(EntityNotFoundException.class);

        verifyNoInteractions(reportServiceClient);
    }

    @Test
    void delete_shouldPassCorrectTrainerInfoToReportService() {
        setAuthenticatedUser("John.Smith");
        when(trainingRepository.findById(10L)).thenReturn(Optional.of(training));

        trainingService.delete(10L);

        ArgumentCaptor<ReportWorkloadRequest> captor =
                ArgumentCaptor.forClass(ReportWorkloadRequest.class);
        verify(reportServiceClient).sendWorkload(captor.capture(), any());

        ReportWorkloadRequest sent = captor.getValue();
        assertThat(sent.getTrainerFirstName()).isEqualTo("John");
        assertThat(sent.getTrainerLastName()).isEqualTo("Smith");
        assertThat(sent.isActive()).isTrue();
    }
}
