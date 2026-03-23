package com.gym_project.repository.impl;

import com.gym_project.dto.filter.TraineeTrainingFilterDto;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.Training;
import com.gym_project.repository.TraineeRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class TraineeRepositoryImpl implements TraineeRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public void save(Trainee trainee) {
        entityManager.persist(trainee);
    }

    @Transactional
    public Trainee update(Trainee trainee) {
        return entityManager.merge(trainee);
    }

    @Transactional
    public void delete(Trainee trainee) {
        entityManager.remove(entityManager.contains(trainee) ? trainee : entityManager.merge(trainee));
    }


    @Transactional(readOnly = true)
    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Trainee.class, id));
    }

    @Transactional(readOnly = true)
    public List<Trainee> findAll() {
        return entityManager.createQuery("SELECT t FROM Trainee t", Trainee.class)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public Optional<Trainee> findByUsername(String username) {
        return entityManager.createQuery(
                        "SELECT t FROM Trainee t WHERE t.username = :username", Trainee.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {

        Long count = entityManager.createQuery(
                        "SELECT COUNT(t) FROM Trainee t WHERE t.username = :username",
                        Long.class)
                .setParameter("username", username)
                .getSingleResult();

        return count > 0;
    }

    @Transactional(readOnly = true)
    public List<String> findUsernamesStartingWith(String prefix) {
        return entityManager.createQuery(
                        "SELECT t.username FROM Trainee t WHERE t.username LIKE :prefix",
                        String.class)
                .setParameter("prefix", prefix + "%")
                .getResultList();
    }

    @Transactional
    public void changePassword(String username, String newPassword) {
        Optional<Trainee> traineeOpt = findByUsername(username);
        if (traineeOpt.isPresent()) {
            Trainee trainee = traineeOpt.get();
            trainee.setPassword(newPassword);
            entityManager.merge(trainee);
        } else {
            throw new IllegalArgumentException("Trainee not found with username " + username);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Training> findTrainingsByTraineeAndFilter(String traineeUsername, TraineeTrainingFilterDto filter) {
        StringBuilder sb = new StringBuilder("SELECT tr FROM Training tr WHERE tr.trainee.username = :username");

        if (filter.getFromDate() != null) sb.append(" AND tr.trainingDate >= :fromDate");
        if (filter.getToDate() != null) sb.append(" AND tr.trainingDate <= :toDate");
        if (filter.getTrainerName() != null && !filter.getTrainerName().isBlank()) {
            sb.append(" AND (tr.trainer.firstName LIKE :trainerName OR tr.trainer.lastName LIKE :trainerName)");
        }
        if (filter.getTrainingTypeName() != null && !filter.getTrainingTypeName().isBlank()) {
            sb.append(" AND tr.trainingType.trainingTypeName = :trainingTypeName");
        }

        var query = entityManager.createQuery(sb.toString(), Training.class)
                .setParameter("username", traineeUsername);

        if (filter.getFromDate() != null) query.setParameter("fromDate", filter.getFromDate());
        if (filter.getToDate() != null) query.setParameter("toDate", filter.getToDate());
        if (filter.getTrainerName() != null && !filter.getTrainerName().isBlank()) {
            query.setParameter("trainerName", "%" + filter.getTrainerName() + "%");
        }
        if (filter.getTrainingTypeName() != null && !filter.getTrainingTypeName().isBlank()) {
            query.setParameter("trainingTypeName", filter.getTrainingTypeName());
        }

        return query.getResultList();
    }

    @Transactional(readOnly = true)
    public List<Trainer> findTrainersByTraineeUsername(String traineeUsername) {
        return entityManager.createQuery(
                        "SELECT tr FROM Trainee t JOIN t.trainers tr WHERE t.username = :username", Trainer.class)
                .setParameter("username", traineeUsername)
                .getResultList();
    }

    @Transactional
    public void deleteByUsername(String username) {
        findByUsername(username).ifPresent(trainee -> {
            entityManager.remove(entityManager.contains(trainee) ? trainee : entityManager.merge(trainee));
        });
    }


}