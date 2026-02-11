package dao;

import model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import storage.EntityName;
import storage.InMemoryStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class TraineeDao {

    private final InMemoryStorage storage;

    @Autowired
    public TraineeDao(InMemoryStorage storage) {
        this.storage = storage;
    }


    public Trainee save(Trainee trainee) {
        List<Trainee> list = storage.getList(EntityName.TRAINEE);

        trainee.setId(generateId(list));
        list.add(trainee);

        return trainee;
    }

    public Trainee update(Trainee trainee) {
        List<Trainee> list = storage.getList(EntityName.TRAINEE);

        list.removeIf(t -> t.getId().equals(trainee.getId()));
        list.add(trainee);

        return trainee;
    }

    public Optional<Trainee> findById(Long id) {
        List<Trainee> list = storage.getTrainees();
        return list.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
    }

    public List<Trainee> findAll() {
        return storage.getList(EntityName.TRAINEE);
    }

    public void delete(Long id) {
        storage.getTrainees().removeIf(t -> t.getId().equals(id));
    }

    private Long generateId(List<Trainee> list) {
        return list.stream()
                .map(Trainee::getId)
                .max(Long::compare)
                .orElse(0L) + 1;
    }
}
