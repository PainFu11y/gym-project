package facade;

import model.Trainee;
import model.Trainer;
import model.Training;
import service.TraineeService;
import service.TrainerService;
import service.TrainingService;

import java.util.List;
import java.util.Optional;

public class TrainingFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

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
        return traineeService.create(trainee);
    }

    public Trainer registerTrainer(Trainer trainer) {
        return trainerService.create(trainer);
    }

    public Training scheduleTraining(Training training) {

        if (traineeService.findById(training.getTraineeId()).isEmpty()) {
            throw new IllegalArgumentException("Trainee not found");
        }
        if (trainerService.findById(training.getTrainerId()).isEmpty()) {
            throw new IllegalArgumentException("Trainer not found");
        }
        return trainingService.create(training);
    }


    public Trainee updateTrainee(Trainee trainee){ return traineeService.update(trainee);}

    public List<Trainer> getAllTrainers() {
        return trainerService.findAll();
    }

    public List<Training> getAllTrainings() {
        return trainingService.findAll();
    }

    public Optional<Training> getTrainingById(Long id){ return trainingService.findById(id);}

    public Optional<Trainer> getTrainerById(Long id){ return trainerService.findById(id);}

    public Optional<Trainee> getTraineeById(Long id){ return traineeService.findById(id);}

    public void deleteTrainee(Long id){ traineeService.delete(id);}


}
