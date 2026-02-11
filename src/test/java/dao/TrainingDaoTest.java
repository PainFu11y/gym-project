package dao;

import gym.dao.TrainingDao;
import gym.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import gym.storage.InMemoryStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TrainingDaoTest {

    private InMemoryStorage storage;
    private TrainingDao trainingDao;

    @BeforeEach
    void setUp() {
        storage = new InMemoryStorage();
        trainingDao = new TrainingDao(storage);
    }

    @Test
    void shouldSaveNewTrainingAndGenerateId() {
        Training training = new Training();
        training.setTrainingName("Morning Cardio");
        training.setTraineeId(1L);
        training.setTrainerId(1L);
        training.setTrainingTypeId(1L);
        training.setTrainingDate(LocalDateTime.now());
        training.setDurationMinutes(60);

        Training saved = trainingDao.save(training);

        assertNotNull(saved.getId());
        assertEquals("Morning Cardio", saved.getTrainingName());
        assertTrue(trainingDao.findAll().contains(saved));
    }


    @Test
    void shouldFindById() {
        Training training = new Training();
        training.setTrainingName("Cardio Blast");
        training.setTraineeId(2L);
        training.setTrainerId(1L);
        training.setTrainingTypeId(3L);

        trainingDao.save(training);

        Optional<Training> found = trainingDao.findById(training.getId());

        assertTrue(found.isPresent());
        assertEquals(training.getId(), found.get().getId());
        assertEquals("Cardio Blast", found.get().getTrainingName());
    }

    @Test
    void shouldReturnEmptyWhenNotFoundById() {
        Optional<Training> found = trainingDao.findById(999L);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnAllTrainings() {
        Training t1 = new Training();
        t1.setTrainingName("Session 1");
        t1.setTraineeId(1L);
        t1.setTrainerId(1L);
        t1.setTrainingTypeId(1L);

        Training t2 = new Training();
        t2.setTrainingName("Session 2");
        t2.setTraineeId(2L);
        t2.setTrainerId(2L);
        t2.setTrainingTypeId(2L);

        trainingDao.save(t1);
        trainingDao.save(t2);

        List<Training> all = trainingDao.findAll();
        assertEquals(2, all.size());
        assertTrue(all.contains(t1));
        assertTrue(all.contains(t2));
    }

    @Test
    void shouldDeleteTrainingById() {
        Training t = new Training();
        t.setTrainingName("Delete Me");
        trainingDao.save(t);

        trainingDao.delete(t.getId());

        List<Training> all = trainingDao.findAll();
        assertTrue(all.isEmpty());
        assertTrue(trainingDao.findById(t.getId()).isEmpty());
    }

    @Test
    void shouldGenerateIncrementalIds() {
        Training t1 = new Training();
        t1.setTrainingName("T1");
        Training t2 = new Training();
        t2.setTrainingName("T2");

        trainingDao.save(t1);
        trainingDao.save(t2);

        assertTrue(t2.getId() > t1.getId());
    }
}
