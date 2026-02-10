package service.impl;

import dao.TrainerDao;
import model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.UserCreationUtil;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private UserCreationUtil userCreationUtil;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainer = new Trainer();
        trainer.setFirstName("Bob");
        trainer.setLastName("Johnson");
        trainer.setPassword("rawPass".toCharArray());
    }


    @Test
    void shouldCreateTrainerAndAssignUsernameAndPassword() {
        doNothing().when(userCreationUtil).assignUsernameAndPassword(trainer);
        when(trainerDao.save(trainer)).thenReturn(trainer);

        Trainer result = trainerService.create(trainer);

        assertNotNull(result);
        assertEquals(trainer, result);

        verify(userCreationUtil).assignUsernameAndPassword(trainer);
        verify(trainerDao).save(trainer);
    }


    @Test
    void shouldUpdateTrainer() {
        when(trainerDao.save(trainer)).thenReturn(trainer);

        Trainer updated = trainerService.update(trainer);

        assertEquals(trainer, updated);
        verify(trainerDao).save(trainer);
        verifyNoInteractions(userCreationUtil);
    }


    @Test
    void shouldFindTrainerById() {
        Long id = 1L;
        when(trainerDao.findById(id)).thenReturn(Optional.of(trainer));

        Optional<Trainer> result = trainerService.findById(id);

        assertTrue(result.isPresent());
        assertEquals(trainer, result.get());
        verify(trainerDao).findById(id);
        verifyNoInteractions(userCreationUtil);
    }

    @Test
    void shouldReturnEmptyWhenTrainerNotFound() {
        Long id = 99L;
        when(trainerDao.findById(id)).thenReturn(Optional.empty());

        Optional<Trainer> result = trainerService.findById(id);

        assertTrue(result.isEmpty());
        verify(trainerDao).findById(id);
        verifyNoInteractions(userCreationUtil);
    }


    @Test
    void shouldReturnAllTrainers() {
        Trainer trainer2 = new Trainer();
        trainer2.setFirstName("Alice");
        trainer2.setLastName("Smith");

        when(trainerDao.findAll()).thenReturn(List.of(trainer, trainer2));

        List<Trainer> result = trainerService.findAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(trainer));
        assertTrue(result.contains(trainer2));

        verify(trainerDao).findAll();
        verifyNoInteractions(userCreationUtil);
    }
}
