package gym.service.impl;

import gym.dao.TraineeDao;
import gym.dao.TrainerDao;
import gym.dao.TrainingDao;
import gym.dao.TrainingTypeDao;
import gym.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import gym.service.TrainingService;

import java.util.List;
import java.util.Optional;

@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Logger logger =
            LoggerFactory.getLogger(TrainingServiceImpl.class);

    private TrainingDao trainingDao;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private TrainingTypeDao trainingTypeDao;


    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setTrainingTypeDao(TrainingTypeDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
    }

    public Training create(Training training) {
        logger.info("Creating training: traineeId={}, trainerId={}, typeId={}",
                training.getTraineeId(),
                training.getTrainerId(),
                training.getTrainingTypeId());
       try{
           if (traineeDao.findById(training.getTraineeId()).isEmpty()) {
               throw new IllegalArgumentException("Trainee not found");
           }

           if (trainerDao.findById(training.getTrainerId()).isEmpty()) {
               throw new IllegalArgumentException("Trainer not found");
           }

           if (trainingTypeDao.findById(training.getTrainingTypeId()).isEmpty()) {
               throw new IllegalArgumentException("Training type not found");
           }
       }catch (Exception e){
           logger.info("Problem during creating training", e);
           return training;
       }


        Training saved = trainingDao.save(training);

        logger.info("Training created successfully with id={}", saved.getId());

        return saved;
    }

    public Optional<Training> findById(Long id) {
        logger.debug("Searching training by id={}", id);

        Optional<Training> training = trainingDao.findById(id);

        if (training.isPresent()) {
            logger.debug("Training found id={}", id);
        } else {
            logger.info("Training not found id={}", id);
        }

        return training;
    }

    public List<Training> findAll() {
        logger.debug("Fetching all trainings");

        List<Training> trainings = trainingDao.findAll();

        logger.debug("Total trainings found: {}", trainings.size());

        return trainings;
    }
}

