package com.gym_project.security;

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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock private TrainerRepository trainerRepository;
    @Mock private TraineeRepository traineeRepository;
    @Mock private AuthService authService;

    @InjectMocks
    private LoginService loginService;

    private Trainee trainee;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setUsername("Jane.Doe");
        trainee.setPassword("traineePass");

        trainer = new Trainer();
        trainer.setUsername("John.Smith");
        trainer.setPassword("trainerPass");
    }


    @Test
    void login_shouldAuthenticateAsTrainer_whenTrainerCredentialsMatch() {
        when(trainerRepository.findByUsernameAndPassword("John.Smith", "trainerPass"))
                .thenReturn(Optional.of(trainer));

        loginService.login("John.Smith", "trainerPass");

        verify(authService).authenticate("John.Smith", Role.TRAINER);
        verifyNoInteractions(traineeRepository);
    }

    @Test
    void login_shouldAuthenticateAsTrainee_whenTraineeCredentialsMatch() {
        when(trainerRepository.findByUsernameAndPassword("Jane.Doe", "traineePass"))
                .thenReturn(Optional.empty());
        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainee));

        loginService.login("Jane.Doe", "traineePass");

        verify(authService).authenticate("Jane.Doe", Role.TRAINEE);
    }

    @Test
    void login_shouldThrowInvalidCredentialsException_whenTraineePasswordWrong() {
        when(trainerRepository.findByUsernameAndPassword("Jane.Doe", "wrongPass"))
                .thenReturn(Optional.empty());
        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainee));

        assertThatThrownBy(() -> loginService.login("Jane.Doe", "wrongPass"))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(authService, never()).authenticate(any(), any());
    }

    @Test
    void login_shouldThrowInvalidCredentialsException_whenUserNotFound() {
        when(trainerRepository.findByUsernameAndPassword("ghost", "any"))
                .thenReturn(Optional.empty());
        when(traineeRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginService.login("ghost", "any"))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(authService, never()).authenticate(any(), any());
    }

    @Test
    void login_shouldCheckTraineeLast_whenTrainerNotFound() {
        when(trainerRepository.findByUsernameAndPassword("Jane.Doe", "traineePass"))
                .thenReturn(Optional.empty());
        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainee));

        loginService.login("Jane.Doe", "traineePass");

        verify(trainerRepository).findByUsernameAndPassword("Jane.Doe", "traineePass");
        verify(traineeRepository).findByUsername("Jane.Doe");
    }

    @Test
    void changePassword_shouldUpdatePassword_forTrainer() {
        when(trainerRepository.findByUsernameAndPassword("John.Smith", "oldPass"))
                .thenReturn(Optional.of(trainer));

        loginService.changePassword("John.Smith", "oldPass", "newPass");

        verify(trainerRepository).changePassword("John.Smith", "newPass");
        verifyNoInteractions(traineeRepository);
    }

    @Test
    void changePassword_shouldUpdatePassword_forTrainee() {
        trainee.setPassword("oldPass");

        when(trainerRepository.findByUsernameAndPassword("Jane.Doe", "oldPass"))
                .thenReturn(Optional.empty());
        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainee));

        loginService.changePassword("Jane.Doe", "oldPass", "newPass");

        verify(traineeRepository).changePassword("Jane.Doe", "newPass");
        verify(trainerRepository, never()).changePassword(any(), any());
    }

    @Test
    void changePassword_shouldThrowInvalidCredentialsException_whenOldPasswordWrong() {
        trainee.setPassword("correctPass");

        when(trainerRepository.findByUsernameAndPassword("Jane.Doe", "wrongOld"))
                .thenReturn(Optional.empty());
        when(traineeRepository.findByUsername("Jane.Doe")).thenReturn(Optional.of(trainee));

        assertThatThrownBy(() -> loginService.changePassword("Jane.Doe", "wrongOld", "newPass"))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(traineeRepository, never()).changePassword(any(), any());
    }

    @Test
    void changePassword_shouldThrowInvalidCredentialsException_whenUserNotFound() {
        when(trainerRepository.findByUsernameAndPassword("ghost", "any"))
                .thenReturn(Optional.empty());
        when(traineeRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginService.changePassword("ghost", "any", "new"))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(trainerRepository, never()).changePassword(any(), any());
        verify(traineeRepository, never()).changePassword(any(), any());
    }
}