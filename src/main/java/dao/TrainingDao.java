package dao;

import model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TrainingDao {

    private Map<Long, Training> storage;

    @Autowired
    public void setStorage(Map<Long, Training> trainingStorage) {
        this.storage = trainingStorage;
    }

    public Training save(Training training) {
        storage.put(training.getId(), training);
        return training;
    }

    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Training> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void delete(Long id) {
        storage.remove(id);
    }
}
