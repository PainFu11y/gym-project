package com.gym_project.security;

import com.gym_project.actuator.metrics.GymMetrics;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.exception.InvalidCredentialsException;
import com.gym_project.repository.TraineeRepository;
import com.gym_project.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock private TrainerRepository trainerRepository;
    @Mock private TraineeRepository traineeRepository;
    @Mock private GymMetrics        gymMetrics;
    @Mock private PasswordEncoder   passwordEncoder;

    @InjectMocks
    private LoginService loginService;

    private Trainee trainee;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainer = new Trainer();
        trainer.setUsername("John.Smith");
        trainer.setPassword("$2a$12$hashedTrainerPass");

        trainee = new Trainee();
        trainee.setUsername("Jane.Doe");
        trainee.setPassword("$2a$12$hashedTraineePass");
    }

    @Test
    void encodePassword_shouldDelegateToPasswordEncoder() {
        when(passwordEncoder.encode("rawPass")).thenReturn("$2a$12$encodedHash");

        String result = loginService.encodePassword("rawPass");

        assertThat(result).isEqualTo("$2a$12$encodedHash");
        verify(passwordEncoder).encode("rawPass");
    }

    @Test
    void recordLoginSuccess_shouldRecordMetric() {
        loginService.recordLoginSuccess();
        verify(gymMetrics).recordLoginSuccess();
    }


    @Test
    void changePassword_shouldUpdatePassword_forTrainer() {
        when(trainerRepository.findByUsername("John.Smith")).thenReturn(Optional.of(trainer));
        when(passwordEncoder.matches("oldPass", "$2a$12$hashedTrainerPass")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("$2a$12$newHashedPass");

        loginService.changePassword("John.Smith", "oldPass", "newPass");

        verify(trainerRepository).changePassword("John.Smith", "$2a$12$newHashedPass");
        verifyNoInteractions(traineeRepository);
    }

    @Test
    void changePassword_shouldThrowInvalidCredentialsException_whenTrainerOldPasswordWrong() {
        when(trainerRepository.findByUsername("John.Smith")).thenReturn(Optional.of(trainer));
        when(passwordEncoder.matches("wrongOld", "$2a$12$hashedTrainerPass")).thenReturn(false);

        assertThatThrownBy(() -> loginService.changePassword("John.Smith", "wrongOld", "newPass"))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(trainerRepository, never()).changePassword(any(), any());
    }

    @Test
    void changePassword_shouldUpdatePassword_forTrainee() {
        when(trainerRepository.findByUsername("Jane.Doe")).thenReturn(Optional.empty());
        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainee));
        when(passwordEncoder.matches("oldPass", "$2a$12$hashedTraineePass")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("$2a$12$newHashedPass");

        loginService.changePassword("Jane.Doe", "oldPass", "newPass");

        verify(traineeRepository).changePassword("Jane.Doe", "$2a$12$newHashedPass");
        verify(trainerRepository, never()).changePassword(any(), any());
    }

    @Test
    void changePassword_shouldThrowInvalidCredentialsException_whenTraineeOldPasswordWrong() {
        when(trainerRepository.findByUsername("Jane.Doe")).thenReturn(Optional.empty());
        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainee));
        when(passwordEncoder.matches("wrongOld", "$2a$12$hashedTraineePass")).thenReturn(false);

        assertThatThrownBy(() -> loginService.changePassword("Jane.Doe", "wrongOld", "newPass"))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(traineeRepository, never()).changePassword(any(), any());
    }

    @Test
    void changePassword_shouldThrowInvalidCredentialsException_whenUserNotFound() {
        when(trainerRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        when(traineeRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginService.changePassword("ghost", "any", "new"))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(trainerRepository, never()).changePassword(any(), any());
        verify(traineeRepository, never()).changePassword(any(), any());
    }

    @Test
    void changePassword_shouldCheckTrainerFirst_thenTrainee() {
        when(trainerRepository.findByUsername("Jane.Doe")).thenReturn(Optional.empty());
        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainee));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$12$newHash");

        loginService.changePassword("Jane.Doe", "oldPass", "newPass");

        verify(trainerRepository).findByUsername("Jane.Doe");
        verify(traineeRepository).findByUsername("Jane.Doe");
    }
}