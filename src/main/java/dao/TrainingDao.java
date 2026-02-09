package dao;

import model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import storage.InMemoryStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TrainingDao {

    private final InMemoryStorage storage;

    @Autowired
    public TrainingDao(InMemoryStorage storage) {
        this.storage = storage;
    }


    public Training save(Training training) {
        List<Training> list = storage.getTrainings();

        training.setId(generateId(list));

        list.removeIf(t -> t.getId().equals(training.getId()));
        list.add(training);

        return training;
    }

    public Optional<Training> findById(Long id) {
        List<Training> list = storage.getTrainings();
        return list.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
    }

    public List<Training> findAll() {
        return storage.getTrainings();
    }

    public void delete(Long id) {
        storage.getTrainings().removeIf(t -> t.getId().equals(id));
    }

    private Long generateId(List<Training> list) {
        return list.stream()
                .map(Training::getId)
                .max(Long::compare)
                .orElse(0L) + 1;
    }
}
