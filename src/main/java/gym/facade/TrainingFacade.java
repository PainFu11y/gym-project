package gym.facade;

import gym.model.Trainee;
import gym.model.Trainer;
import gym.model.Training;
import gym.service.TraineeService;
import gym.service.TrainerService;
import gym.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TrainingFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private static final Logger logger =
            LoggerFactory.getLogger(TrainingFacade.class);

    public TrainingFacade(
            TraineeService traineeService,
            TrainerService trainerService,
            TrainingService trainingService
    ) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public Trainee registerTrainee(Trainee trainee) {
        try {
            trainee = traineeService.create(trainee);

        } catch (IllegalArgumentException e) {
            logger.info("Problem during creating trainee", e);
        } catch (Exception e) {
            logger.warn("Something went wrong during creating trainee", e);
        }
        return trainee;
    }

    public Trainer registerTrainer(Trainer trainer) {
        try {
            return trainerService.create(trainer);
        } catch (IllegalArgumentException e) {
            logger.info("Problem during creating trainer with id: {}",trainer.getId(), e);
        } catch (Exception e) {
            logger.warn("Something went wrong during creating trainer with id: {}",trainer.getId(), e);
        }
        return trainer;
    }

    public Training scheduleTraining(Training training) {
        try {
            return trainingService.create(training);
        } catch (IllegalArgumentException e) {
            logger.info("Problem during creating training with traineeID: {}, trainerID: {}",
                    training.getTraineeId(),training.getTrainerId(), e);
        } catch (Exception e) {
            logger.warn("Something went wrong during creating trainee", e);
        }
        return training;
    }


    public void updateTrainee(Trainee trainee) {
        try {
            traineeService.update(trainee);
        } catch (IllegalArgumentException e) {
            logger.info("Problem during creating trainee with id: {}",trainee.getId(), e);
        } catch (Exception e) {
            logger.warn("Something went wrong during creating trainee", e);
        }
    }

    public void updateTrainer(Trainer trainer) {
        try {
            trainerService.update(trainer);
        } catch (IllegalArgumentException e) {
            logger.info("Problem during creating trainer with id: {}",trainer.getId(), e);
        } catch (Exception e) {
            logger.warn("Something went wrong during creating trainee", e);
        }
    }

    public List<Trainer> getAllTrainers() {
        return trainerService.findAll();
    }

    public List<Training> getAllTrainings() {
        return trainingService.findAll();
    }

    public Optional<Training> getTrainingById(Long id) {
        return trainingService.findById(id);
    }

    public Optional<Trainer> getTrainerById(Long id) {
        return trainerService.findById(id);
    }

    public Optional<Trainee> getTraineeById(Long id) {
        return traineeService.findById(id);
    }

    public void deleteTrainee(Long id) {
        traineeService.delete(id);
    }


}
