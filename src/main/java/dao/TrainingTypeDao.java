package dao;

import model.TrainingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import storage.InMemoryStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingTypeDao {
    private final InMemoryStorage storage;

    @Autowired
    public TrainingTypeDao(InMemoryStorage storage) {
        this.storage = storage;
    }


    public TrainingType save(TrainingType trainingType) {
        List<TrainingType> list = storage.getTrainingTypes();

        if (trainingType.getId() == null) {
            trainingType.setId(generateId(list));
        }

        list.removeIf(t -> t.getId().equals(trainingType.getId()));
        list.add(trainingType);

        return trainingType;
    }

    public Optional<TrainingType> findById(Long id) {
        List<TrainingType> list = storage.getTrainingTypes();
        return list.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
    }

    public List<TrainingType> findAll() {
        return storage.getTrainingTypes();
    }

    public void delete(Long id) {
        storage.getTrainingTypes().removeIf(t -> t.getId().equals(id));
    }

    private Long generateId(List<TrainingType> list) {
        return list.stream()
                .map(TrainingType::getId)
                .max(Long::compare)
                .orElse(0L) + 1;
    }
}
