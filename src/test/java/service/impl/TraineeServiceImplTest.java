package service.impl;

import dao.TraineeDao;
import model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.UserCreationUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private UserCreationUtil userCreationUtil;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setFirstName("Alice");
        trainee.setLastName("Smith");
        trainee.setPassword("rawPass".toCharArray());
    }


    @Test
    void shouldCreateTraineeAndAssignUsernameAndPassword() {
        doNothing().when(userCreationUtil).assignUsernameAndPassword(trainee);

        when(traineeDao.save(trainee)).thenReturn(trainee);

        Trainee result = traineeService.create(trainee);

        assertNotNull(result);
        assertEquals(trainee, result);

        verify(userCreationUtil).assignUsernameAndPassword(trainee);
        verify(traineeDao).save(trainee);
    }


    @Test
    void shouldUpdateTrainee() {
        when(traineeDao.save(trainee)).thenReturn(trainee);

        Trainee updated = traineeService.update(trainee);

        assertEquals(trainee, updated);
        verify(traineeDao).save(trainee);
        verifyNoInteractions(userCreationUtil);
    }


    @Test
    void shouldDeleteTraineeById() {
        Long id = 1L;
        doNothing().when(traineeDao).delete(id);

        traineeService.delete(id);

        verify(traineeDao).delete(id);
        verifyNoInteractions(userCreationUtil);
    }


    @Test
    void shouldFindTraineeById() {
        Long id = 1L;
        when(traineeDao.findById(id)).thenReturn(Optional.of(trainee));

        Optional<Trainee> result = traineeService.findById(id);

        assertTrue(result.isPresent());
        assertEquals(trainee, result.get());
        verify(traineeDao).findById(id);
        verifyNoInteractions(userCreationUtil);
    }

    @Test
    void shouldReturnEmptyWhenTraineeNotFound() {
        Long id = 99L;
        when(traineeDao.findById(id)).thenReturn(Optional.empty());

        Optional<Trainee> result = traineeService.findById(id);

        assertTrue(result.isEmpty());
        verify(traineeDao).findById(id);
        verifyNoInteractions(userCreationUtil);
    }
}
