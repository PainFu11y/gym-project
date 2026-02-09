package dao;

import model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TrainerDao {

    private Map<Long, Trainer> storage;

    @Autowired
    public void setStorage(Map<Long, Trainer> trainerStorage) {
        this.storage = trainerStorage;
    }

    public Trainer save(Trainer trainer) {
        storage.put(trainer.getId(), trainer);
        return trainer;
    }

    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Trainer> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void delete(Long id) {
        storage.remove(id);
    }
}
