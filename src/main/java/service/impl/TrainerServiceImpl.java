package service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.UserCreationUtil;
import model.Trainer;
import dao.TrainerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.TrainerService;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerServiceImpl implements TrainerService {

    private final TrainerDao trainerDao;
    private final UserCreationUtil userCreationUtil;
    private static final Logger logger =
            LoggerFactory.getLogger(TrainerServiceImpl.class);


    @Autowired
    public TrainerServiceImpl(TrainerDao trainerDao, UserCreationUtil userCreationUtil) {
        this.trainerDao = trainerDao;
        this.userCreationUtil = userCreationUtil;
    }

    public Trainer create(Trainer trainer) {
        logger.info("Creating trainer: {} {}",
                trainer.getFirstName(), trainer.getLastName());

        userCreationUtil.assignUsernameAndPassword(trainer);
        Trainer saved = trainerDao.save(trainer);

        logger.info("Trainer created with id={}", saved.getId());

        return saved;
    }

    public Trainer update(Trainer trainer) {
        logger.info("Updating trainer id={}", trainer.getId());

        Trainer existing = trainerDao.findById(trainer.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Trainer not found")
                );

        trainer.setUsername(existing.getUsername());
        trainer.setPassword(existing.getPassword());

        Trainer updated = trainerDao.update(trainer);

        logger.info("Trainer updated successfully id={}", updated.getId());

        return updated;
    }

    public Optional<Trainer> findById(Long id) {
        logger.debug("Searching trainer by id={}", id);

        Optional<Trainer> trainer = trainerDao.findById(id);

        if (trainer.isPresent()) {
            logger.debug("Trainer found id={}", id);
        } else {
            logger.info("Trainer not found id={}", id);
        }

        return trainer;
    }

    public List<Trainer> findAll() {
        logger.debug("Fetching all trainers");

        List<Trainer> trainers = trainerDao.findAll();

        logger.debug("Total trainers found: {}", trainers.size());

        return trainers;
    }
}


