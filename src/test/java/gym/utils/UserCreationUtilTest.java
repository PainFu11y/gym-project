package gym.utils;

import gym.dao.TraineeDao;
import gym.dao.TrainerDao;
import gym.model.Trainee;
import gym.model.Trainer;
import gym.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCreationUtilTest {

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private UserCreationUtil userCreationUtil;

    private User user;

    @BeforeEach
    void setUp() {
        user = new Trainer();
        user.setFirstName("John");
        user.setLastName("Doe");
    }


    @Test
    void shouldAssignUsernameAndGeneratedPasswordForTrainer() {
        Trainer existingTrainer = mock(Trainer.class);
        when(existingTrainer.getUsername()).thenReturn("existing.trainer");

        Trainee existingTrainee = mock(Trainee.class);
        when(existingTrainee.getUsername()).thenReturn("existing.trainee");

        when(trainerDao.findAll()).thenReturn(List.of(existingTrainer));
        when(traineeDao.findAll()).thenReturn(List.of(existingTrainee));

        when(usernameGenerator.generate(
                eq("John"),
                eq("Doe"),
                eq(Set.of("existing.trainer", "existing.trainee"))
        )).thenReturn("john.doe");

        when(passwordGenerator.generate()).thenReturn("GeneratedPass1");

        userCreationUtil.assignUsernameAndPassword(user);

        assertEquals("john.doe", user.getUsername());
        assertArrayEquals("GeneratedPass1".toCharArray(), user.getPassword());

        verify(trainerDao).findAll();
        verify(traineeDao).findAll();
        verify(usernameGenerator).generate(any(), any(), any());
        verify(passwordGenerator).generate();
    }

    @Test
    void shouldThrowExceptionWhenUserIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                userCreationUtil.assignUsernameAndPassword(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenFirstNameIsBlank() {
        user.setFirstName(" ");

        assertThrows(IllegalArgumentException.class, () ->
                userCreationUtil.assignUsernameAndPassword(user)
        );
    }

    @Test
    void shouldThrowExceptionWhenLastNameIsNull() {
        user.setLastName(null);

        assertThrows(IllegalArgumentException.class, () ->
                userCreationUtil.assignUsernameAndPassword(user)
        );
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadySet() {
        user.setUsername("already.set");

        assertThrows(IllegalStateException.class, () ->
                userCreationUtil.assignUsernameAndPassword(user)
        );
    }

    @Test
    void shouldNotCallDependenciesWhenValidationFails() {
        user.setFirstName("");

        assertThrows(IllegalArgumentException.class, () ->
                userCreationUtil.assignUsernameAndPassword(user)
        );

        verifyNoInteractions(trainerDao, traineeDao, usernameGenerator, passwordGenerator);
    }
}
