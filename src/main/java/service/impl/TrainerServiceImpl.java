package service.impl;

import utils.UserCreationUtil;
import model.Trainer;
import dao.TrainerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.TrainerService;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerServiceImpl implements TrainerService {

    private final TrainerDao trainerDao;
    private final UserCreationUtil userCreationUtil;

    @Autowired
    public TrainerServiceImpl(TrainerDao trainerDao, UserCreationUtil userCreationUtil) {
        this.trainerDao = trainerDao;
        this.userCreationUtil = userCreationUtil;
    }

    public Trainer create(Trainer trainer) {
        userCreationUtil.assignUsernameAndPassword(trainer);
        return trainerDao.save(trainer);
    }

    public Trainer update(Trainer trainer) {

        Trainer existing = trainerDao.findById(trainer.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Trainer not found")
                );

        trainer.setUsername(existing.getUsername());
        trainer.setPassword(existing.getPassword());

        return trainerDao.update(trainer);
    }

    public Optional<Trainer> findById(Long id) {
        return trainerDao.findById(id);
    }

    public List<Trainer> findAll() {
        return trainerDao.findAll();
    }
}


