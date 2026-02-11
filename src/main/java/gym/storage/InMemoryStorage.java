package gym.storage;

import gym.model.Trainee;
import gym.model.Trainer;
import gym.model.Training;
import gym.model.TrainingType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryStorage {

    private final Map<EntityName, List<?>> storage = new HashMap<>();


    public InMemoryStorage() {
        storage.put(EntityName.TRAINER, new ArrayList<Trainer>());
        storage.put(EntityName.TRAINEE, new ArrayList<Trainee>());
        storage.put(EntityName.TRAINING, new ArrayList<Training>());
        storage.put(EntityName.TRAINING_TYPE, new ArrayList<TrainingType>());
    }


    @SuppressWarnings("unchecked")
    public <T> List<T> getList(EntityName key) {
        return (List<T>) storage.get(key);
    }

    @SuppressWarnings("unchecked")
    public List<Trainee> getTrainees() {
        return (List<Trainee>) storage.get(EntityName.TRAINEE);
    }

    @SuppressWarnings("unchecked")
    public List<Trainer> getTrainers() {
        return (List<Trainer>) storage.get(EntityName.TRAINER);
    }

    @SuppressWarnings("unchecked")
    public List<Training> getTrainings() {
        return (List<Training>) storage.get(EntityName.TRAINING);
    }

    @SuppressWarnings("unchecked")
    public List<TrainingType> getTrainingTypes() {
        return (List<TrainingType>) storage.get(EntityName.TRAINING_TYPE);
    }

}
