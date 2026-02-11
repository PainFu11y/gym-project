package dao;

import gym.dao.TrainingTypeDao;
import gym.model.TrainingType;
import gym.model.enums.TrainingTypeName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import gym.storage.InMemoryStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TrainingTypeDaoTest {

    private InMemoryStorage storage;
    private TrainingTypeDao trainingTypeDao;

    @BeforeEach
    void setUp() {
        storage = new InMemoryStorage();
        trainingTypeDao = new TrainingTypeDao(storage);
    }

    @Test
    void shouldSaveNewTrainingTypeAndGenerateId() {
        TrainingType type = new TrainingType();
        type.setName(TrainingTypeName.SWIMMING);

        TrainingType saved = trainingTypeDao.save(type);

        assertNotNull(saved.getId());
        assertEquals(TrainingTypeName.SWIMMING, saved.getName());

        List<TrainingType> all = trainingTypeDao.findAll();
        assertTrue(all.contains(saved));
    }

    @Test
    void shouldUpdateExistingTrainingType() {
        TrainingType type = new TrainingType();
        type.setName(TrainingTypeName.AEROBICS);

        trainingTypeDao.save(type);
        Long id = type.getId();


        type.setName(TrainingTypeName.CARDIO);
        TrainingType updated = trainingTypeDao.save(type);

        assertEquals(id, updated.getId());
        assertEquals(TrainingTypeName.CARDIO, updated.getName());


        Optional<TrainingType> found = trainingTypeDao.findById(id);
        assertTrue(found.isPresent());
        assertEquals(TrainingTypeName.CARDIO, found.get().getName());
    }

    @Test
    void shouldFindById() {
        TrainingType type = new TrainingType();
        type.setName(TrainingTypeName.YOGA);
        trainingTypeDao.save(type);

        Optional<TrainingType> found = trainingTypeDao.findById(type.getId());

        assertTrue(found.isPresent());
        assertEquals(type.getId(), found.get().getId());
        assertEquals(TrainingTypeName.YOGA, found.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenNotFoundById() {
        Optional<TrainingType> found = trainingTypeDao.findById(999L);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnAllTrainingTypes() {
        TrainingType t1 = new TrainingType();
        t1.setName(TrainingTypeName.SWIMMING);
        TrainingType t2 = new TrainingType();
        t2.setName(TrainingTypeName.CALISTHENICS);

        trainingTypeDao.save(t1);
        trainingTypeDao.save(t2);

        List<TrainingType> all = trainingTypeDao.findAll();

        assertEquals(2, all.size());
        assertTrue(all.contains(t1));
        assertTrue(all.contains(t2));
    }

    @Test
    void shouldDeleteTrainingTypeById() {
        TrainingType t = new TrainingType();
        t.setName(TrainingTypeName.CARDIO);
        trainingTypeDao.save(t);

        trainingTypeDao.delete(t.getId());

        List<TrainingType> all = trainingTypeDao.findAll();
        assertTrue(all.isEmpty());
        assertTrue(trainingTypeDao.findById(t.getId()).isEmpty());
    }

    @Test
    void shouldGenerateIncrementalIds() {
        TrainingType t1 = new TrainingType();
        t1.setName(TrainingTypeName.SWIMMING);
        TrainingType t2 = new TrainingType();
        t2.setName(TrainingTypeName.YOGA);

        trainingTypeDao.save(t1);
        trainingTypeDao.save(t2);

        assertTrue(t2.getId() > t1.getId());
    }
}
