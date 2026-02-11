package dao;

import model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import storage.InMemoryStorage;

import java.util.List;
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

        list.add(trainer);

        return trainer;
    }

    public Trainer update(Trainer trainer) {
        List<Trainer> list = storage.getTrainers();

        Trainer existing = findById(trainer.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Trainer not found with id: " + trainer.getId())
                );

        list.remove(existing);
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


    private Long generateId(List<Trainer> list) {
        return list.stream()
                .map(Trainer::getId)
                .max(Long::compare)
                .orElse(0L) + 1;
    }
}
