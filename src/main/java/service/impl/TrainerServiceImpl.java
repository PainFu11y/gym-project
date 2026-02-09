package service.impl;

import Utils.UserCreationUtil;
import model.Trainer;
import Utils.PasswordGenerator;
import Utils.UsernameGenerator;
import dao.TraineeDao;
import dao.TrainerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerServiceImpl {

    private UserCreationUtil userCreationUtil;
    private TrainerDao trainerDao;
    private TraineeDao traineeDao;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) {
        this.usernameGenerator = usernameGenerator;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    public Trainer create(Trainer trainer) {

        userCreationUtil.assignUsernameAndPassword(trainer);
        return trainerDao.save(trainer);
    }

    public Trainer update(Trainer trainer) {
        return trainerDao.save(trainer);
    }

    public Optional<Trainer> findById(Long id) {
        return trainerDao.findById(id);
    }

    public List<Trainer> findAll() {
        return trainerDao.findAll();
    }
}

