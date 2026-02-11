package dao;

import model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import storage.InMemoryStorage;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TraineeDaoTest {

    private TraineeDao traineeDao;
    private InMemoryStorage storage;

    @BeforeEach
    void setUp() {
        storage = new InMemoryStorage();
        traineeDao = new TraineeDao(storage);
    }

    @Test
    void shouldSaveTraineeAndGenerateId() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Alice");

        Trainee saved = traineeDao.save(trainee);

        assertNotNull(saved.getId());
        assertEquals(1L, saved.getId());
        assertEquals(1, traineeDao.findAll().size());
    }

    @Test
    void shouldUpdateTrainee() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Alice");

        Trainee saved = traineeDao.save(trainee);

        saved.setFirstName("Updated");

        Trainee updated = traineeDao.update(saved);

        Optional<Trainee> found = traineeDao.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Updated", found.get().getFirstName());
    }

    @Test
    void shouldFindById() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Bob");

        Trainee saved = traineeDao.save(trainee);

        Optional<Trainee> found = traineeDao.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        Optional<Trainee> found = traineeDao.findById(999L);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldDeleteTrainee() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Charlie");

        Trainee saved = traineeDao.save(trainee);

        traineeDao.delete(saved.getId());

        Optional<Trainee> found = traineeDao.findById(saved.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnAllTrainees() {
        traineeDao.save(new Trainee());
        traineeDao.save(new Trainee());

        assertEquals(2, traineeDao.findAll().size());
    }
}
