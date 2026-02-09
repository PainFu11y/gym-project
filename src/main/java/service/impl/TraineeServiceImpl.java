package service.impl;

import Utils.PasswordGenerator;
import Utils.UserCreationUtil;
import Utils.UsernameGenerator;
import dao.TraineeDao;
import dao.TrainerDao;
import model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TraineeServiceImpl {

    private UserCreationUtil userCreationUtil;
    private TrainerDao trainerDao;
    private TraineeDao traineeDao;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;

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

    public Trainee create(Trainee trainee) {

        userCreationUtil.assignUsernameAndPassword(trainee);
        return traineeDao.save(trainee);
    }

    public Trainee update(Trainee trainee) {
        return traineeDao.save(trainee);
    }

    public void delete(Long id) {
        traineeDao.delete(id);
    }

    public Optional<Trainee> findById(Long id) {
        return traineeDao.findById(id);
    }
}
