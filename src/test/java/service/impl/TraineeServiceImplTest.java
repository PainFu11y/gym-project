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

        Long id = 1L;

        Trainee existing = new Trainee();
        existing.setId(id);
        existing.setUsername("alice.smith");
        existing.setPassword("encodedPass".toCharArray());

        trainee.setId(id);
        trainee.setUsername("hacked");
        trainee.setPassword("newPass".toCharArray()); 

        when(traineeDao.findById(id)).thenReturn(Optional.of(existing));
        when(traineeDao.update(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainee updated = traineeService.update(trainee);

        assertEquals("alice.smith", updated.getUsername());
        assertArrayEquals("encodedPass".toCharArray(), updated.getPassword());

        verify(traineeDao).findById(id);
        verify(traineeDao).update(trainee);
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
