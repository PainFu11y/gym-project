package dao;

import model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import storage.InMemoryStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TrainerDao {

    private final InMemoryStorage storage;

    @Autowired
    public TrainerDao(InMemoryStorage storage) {
        this.storage = storage;
    }


    public Trainer save(Trainer trainer) {
        List<Trainer> list = storage.getTrainers();

        trainer.setId(generateId(list));

        list.removeIf(t -> t.getId().equals(trainer.getId()));
        list.add(trainer);

        return trainer;
    }

    public Optional<Trainer> findById(Long id) {
        List<Trainer> list = storage.getTrainers();
        return list.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
    }

    public List<Trainer> findAll() {
        return storage.getTrainers();
    }

    public void delete(Long id) {
        storage.getTrainers().removeIf(t -> t.getId().equals(id));
    }

    private Long generateId(List<Trainer> list) {
        return list.stream()
                .map(Trainer::getId)
                .max(Long::compare)
                .orElse(0L) + 1;
    }
}
