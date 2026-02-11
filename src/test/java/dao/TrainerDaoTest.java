package dao;

import model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import storage.InMemoryStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TrainerDaoTest {

    private TrainerDao trainerDao;

    @BeforeEach
    void setUp() {
        InMemoryStorage storage = new InMemoryStorage();
        trainerDao = new TrainerDao(storage);
    }

    @Test
    void shouldSaveTrainerAndGenerateId() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("John");
        trainer.setLastName("Doe");

        Trainer saved = trainerDao.save(trainer);

        assertNotNull(saved.getId());
        assertEquals("John", saved.getFirstName());
        assertEquals("Doe", saved.getLastName());

        assertTrue(trainerDao.findAll().contains(saved));
    }

    @Test
    void shouldFindTrainerById() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Bob");

        trainerDao.save(trainer);

        Optional<Trainer> found = trainerDao.findById(trainer.getId());

        assertTrue(found.isPresent());
        assertEquals(trainer.getId(), found.get().getId());
    }

    @Test
    void shouldReturnEmptyOptionalWhenTrainerNotFound() {
        Optional<Trainer> found = trainerDao.findById(999L);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnAllTrainers() {
        Trainer t1 = new Trainer();
        t1.setFirstName("T1");

        Trainer t2 = new Trainer();
        t2.setFirstName("T2");

        trainerDao.save(t1);
        trainerDao.save(t2);

        List<Trainer> all = trainerDao.findAll();

        assertEquals(2, all.size());
        assertTrue(all.contains(t1));
        assertTrue(all.contains(t2));
    }

    @Test
    void shouldGenerateIncrementalIds() {
        Trainer t1 = new Trainer();
        Trainer t2 = new Trainer();

        trainerDao.save(t1);
        trainerDao.save(t2);

        assertTrue(t2.getId() > t1.getId());
    }
}
