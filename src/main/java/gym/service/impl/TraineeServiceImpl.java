package gym.service.impl;

import gym.utils.UserCreationUtil;
import gym.dao.TraineeDao;
import gym.model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import gym.service.TraineeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;

@Service
public class TraineeServiceImpl implements TraineeService {

    private final TraineeDao traineeDao;
    private final UserCreationUtil userCreationUtil;
    private static final Logger logger =
            LoggerFactory.getLogger(TraineeServiceImpl.class);

    @Autowired
    public TraineeServiceImpl(TraineeDao traineeDao, UserCreationUtil userCreationUtil) {
        this.traineeDao = traineeDao;
        this.userCreationUtil = userCreationUtil;
    }

    public Trainee create(Trainee trainee) {
        logger.info("Creating trainee: {} {}",
                trainee.getFirstName(),
                trainee.getLastName());

        try{
            userCreationUtil.assignUsernameAndPassword(trainee);
        } catch (Exception ex){
            logger.info("Problem during saving user", ex);
            return trainee;
        }

        trainee.setDateOfBirth(LocalDate.of(2001, 12, 31));
        trainee.setAddress("Pushkin Street, Kolotushkin House");
        Trainee saved = traineeDao.save(trainee);

        logger.info("Trainee created with id={}", saved.getId());

        return saved;
    }

    public Trainee update(Trainee trainee) {

        logger.info("Updating trainee with id={}", trainee.getId());

        Trainee existing = traineeDao.findById(trainee.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Trainee not found")
                );

        trainee.setUsername(existing.getUsername());
        trainee.setPassword(existing.getPassword());


        Trainee updated = traineeDao.update(trainee);

        logger.info("Trainee updated successfully id={}", updated.getId());

        return updated;
    }


    public void delete(Long id) {
        logger.info("Deleting trainee with id={}", id);

        traineeDao.delete(id);

        logger.info("Trainee successfully deleted id={}", id);
    }

    public Optional<Trainee> findById(Long id) {
        logger.debug("Searching trainee by id={}", id);

        Optional<Trainee> trainee = traineeDao.findById(id);

        if (trainee.isPresent()) {
            logger.debug("Trainee found id={}", id);
        } else {
            logger.info("Trainee not found id={}", id);
        }

        return trainee;
    }
}

