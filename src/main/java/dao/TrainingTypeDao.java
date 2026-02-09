package dao;

import model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TrainingTypeDao {
    private Map<Long, Trainee> storage;

    @Autowired
    public void setStorage(Map<Long, Trainee> traineeStorage) {
        this.storage = traineeStorage;
    }

    public Trainee save(Trainee trainee) {
        storage.put(trainee.getId(), trainee);
        return trainee;
    }

    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Trainee> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void delete(Long id) {
        storage.remove(id);
    }
}
