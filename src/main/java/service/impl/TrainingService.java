package service.impl;

import dao.TraineeDao;
import dao.TrainerDao;
import dao.TrainingDao;
import dao.TrainingTypeDao;
import model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainingService {

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

        if (traineeDao.findById(training.getTraineeId()).isEmpty()) {
            throw new IllegalArgumentException("Trainee not found");
        }

        if (trainerDao.findById(training.getTrainerId()).isEmpty()) {
            throw new IllegalArgumentException("Trainer not found");
        }

        if (trainingTypeDao.findById(training.getTrainingTypeId()).isEmpty()) {
            throw new IllegalArgumentException("Training type not found");
        }

        return trainingDao.save(training);
    }

    public Optional<Training> findById(Long id) {
        return trainingDao.findById(id);
    }

    public List<Training> findAll() {
        return trainingDao.findAll();
    }
}

