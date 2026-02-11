package service.impl;

import utils.UserCreationUtil;
import dao.TraineeDao;
import model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.TraineeService;

import java.util.*;

@Service
public class TraineeServiceImpl implements TraineeService {

    private final TraineeDao traineeDao;
    private final UserCreationUtil userCreationUtil;

    @Autowired
    public TraineeServiceImpl(TraineeDao traineeDao, UserCreationUtil userCreationUtil) {
        this.traineeDao = traineeDao;
        this.userCreationUtil = userCreationUtil;
    }

    public Trainee create(Trainee trainee) {
        userCreationUtil.assignUsernameAndPassword(trainee);
        return traineeDao.save(trainee);
    }

    public Trainee update(Trainee trainee) {

        Trainee existing = traineeDao.findById(trainee.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Trainee not found")
                );

        trainee.setUsername(existing.getUsername());
        trainee.setPassword(existing.getPassword());

        return traineeDao.update(trainee);
    }


    public void delete(Long id) {
        traineeDao.delete(id);
    }

    public Optional<Trainee> findById(Long id) {
        return traineeDao.findById(id);
    }
}

