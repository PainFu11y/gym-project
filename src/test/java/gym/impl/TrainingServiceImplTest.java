package gym.impl;

import gym.dao.TraineeDao;
import gym.dao.TrainerDao;
import gym.dao.TrainingDao;
import gym.dao.TrainingTypeDao;
import gym.model.Training;
import gym.model.Trainee;
import gym.model.Trainer;
import gym.model.TrainingType;
import gym.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Training training;

    @BeforeEach
    void setUp() {
        training = new Training();
        training.setId(1L);
        training.setTraineeId(10L);
        training.setTrainerId(20L);
        training.setTrainingTypeId(30L);
        training.setTrainingName("Morning Session");
        training.setTrainingDate(LocalDateTime.now());
        training.setDurationMinutes(60);
    }


    @Test
    void shouldCreateTrainingWhenAllExist() {
        when(traineeDao.findById(10L)).thenReturn(Optional.of(new Trainee()));
        when(trainerDao.findById(20L)).thenReturn(Optional.of(new Trainer()));
        when(trainingTypeDao.findById(30L)).thenReturn(Optional.of(new TrainingType()));
        when(trainingDao.save(training)).thenReturn(training);

        Training result = trainingService.create(training);

        assertNotNull(result);
        assertEquals(training, result);

        verify(traineeDao).findById(10L);
        verify(trainerDao).findById(20L);
        verify(trainingTypeDao).findById(30L);
        verify(trainingDao).save(training);
    }

    @Test
    void shouldThrowExceptionIfTraineeNotFound() {
        when(traineeDao.findById(10L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> trainingService.create(training)
        );

        assertEquals("Trainee not found", exception.getMessage());

        verify(traineeDao).findById(10L);
        verifyNoInteractions(trainerDao, trainingTypeDao, trainingDao);
    }

    @Test
    void shouldThrowExceptionIfTrainerNotFound() {
        when(traineeDao.findById(10L)).thenReturn(Optional.of(new Trainee()));
        when(trainerDao.findById(20L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> trainingService.create(training)
        );

        assertEquals("Trainer not found", exception.getMessage());

        verify(traineeDao).findById(10L);
        verify(trainerDao).findById(20L);
        verifyNoInteractions(trainingTypeDao, trainingDao);
    }

    @Test
    void shouldThrowExceptionIfTrainingTypeNotFound() {
        when(traineeDao.findById(10L)).thenReturn(Optional.of(new Trainee()));
        when(trainerDao.findById(20L)).thenReturn(Optional.of(new Trainer()));
        when(trainingTypeDao.findById(30L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> trainingService.create(training)
        );

        assertEquals("Training type not found", exception.getMessage());

        verify(traineeDao).findById(10L);
        verify(trainerDao).findById(20L);
        verify(trainingTypeDao).findById(30L);
        verifyNoInteractions(trainingDao);
    }


    @Test
    void shouldFindTrainingById() {
        when(trainingDao.findById(1L)).thenReturn(Optional.of(training));

        Optional<Training> result = trainingService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(training, result.get());
        verify(trainingDao).findById(1L);
        verifyNoInteractions(traineeDao, trainerDao, trainingTypeDao);
    }

    @Test
    void shouldReturnEmptyWhenTrainingNotFound() {
        when(trainingDao.findById(99L)).thenReturn(Optional.empty());

        Optional<Training> result = trainingService.findById(99L);

        assertTrue(result.isEmpty());
        verify(trainingDao).findById(99L);
        verifyNoInteractions(traineeDao, trainerDao, trainingTypeDao);
    }


    @Test
    void shouldReturnAllTrainings() {
        Training training2 = new Training();
        training2.setId(2L);
        when(trainingDao.findAll()).thenReturn(List.of(training, training2));

        List<Training> result = trainingService.findAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(training));
        assertTrue(result.contains(training2));

        verify(trainingDao).findAll();
        verifyNoInteractions(traineeDao, trainerDao, trainingTypeDao);
    }
}
