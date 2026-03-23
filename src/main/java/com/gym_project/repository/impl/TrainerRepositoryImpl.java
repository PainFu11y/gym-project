package com.gym_project.repository.impl;

import com.gym_project.dto.filter.TrainerTrainingFilterDto;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.Training;
import com.gym_project.repository.TrainerRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainerRepositoryImpl implements TrainerRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional
    public void save(Trainer trainer) {
        entityManager.persist(trainer);
    }

    @Override
    @Transactional
    public Trainer update(Trainer trainer) {
        return entityManager.merge(trainer);
    }

    @Override
    @Transactional
    public void delete(Trainer trainer) {
        entityManager.remove(entityManager.contains(trainer) ? trainer : entityManager.merge(trainer));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Trainer.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> findAll() {
        return entityManager.createQuery("SELECT t FROM Trainer t", Trainer.class)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> findBySpecialization(String specialization) {
        return entityManager.createQuery("SELECT t FROM Trainer t WHERE t.specialization = :spec", Trainer.class)
                .setParameter("spec", specialization)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainer> findByUsernameAndPassword(String username, String password) {
        return entityManager.createQuery(
                        "SELECT t FROM Trainer t WHERE t.username = :username AND t.password = :password", Trainer.class)
                .setParameter("username", username)
                .setParameter("password", password)
                .getResultStream()
                .findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainer> findByUsername(String username) {
        return entityManager.createQuery(
                        "SELECT t FROM Trainer t WHERE t.username = :username", Trainer.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    @Override
    @Transactional
    public void changePassword(String username, String newPassword) {
        Optional<Trainer> traineeOpt = findByUsername(username);
        if (traineeOpt.isPresent()) {
            Trainer trainer = traineeOpt.get();
            trainer.setPassword(newPassword);
            entityManager.merge(trainer);
        } else {
            throw new IllegalArgumentException("Trainer not found with username " + username);
        }
    }

    @Override
    @Transactional
    public void deleteByUsername(String username) {
        findByUsername(username).ifPresent(trainer -> {
            entityManager.remove(entityManager.contains(trainer) ? trainer : entityManager.merge(trainer));
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> findUnassignedTrainersByTraineeUsername(String traineeUsername) {
        return entityManager.createQuery(
                        "SELECT tr FROM Trainer tr " +
                                "WHERE :trainee NOT MEMBER OF tr.trainees", Trainer.class)
                .setParameter("trainee",
                        entityManager.createQuery(
                                        "SELECT t FROM Trainee t WHERE t.username = :username", Trainee.class)
                                .setParameter("username", traineeUsername)
                                .getSingleResult()
                )
                .getResultList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> findUsernamesStartingWith(String base) {
        return entityManager.createQuery(
                        "SELECT t.username FROM Trainer t WHERE t.username LIKE :pattern", String.class)
                .setParameter("pattern", base + "%")
                .getResultList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Training> findTrainingsByTrainerAndFilter(String trainerUsername, TrainerTrainingFilterDto filter) {
        StringBuilder sb = new StringBuilder("SELECT tr FROM Training tr WHERE tr.trainer.username = :username");

        if (filter.getFromDate() != null) sb.append(" AND tr.trainingDate >= :fromDate");
        if (filter.getToDate() != null) sb.append(" AND tr.trainingDate <= :toDate");
        if (filter.getTraineeName() != null && !filter.getTraineeName().isBlank()) {
            sb.append(" AND (tr.trainee.firstName LIKE :traineeName OR tr.trainee.lastName LIKE :traineeName)");
        }

        var query = entityManager.createQuery(sb.toString(), Training.class)
                .setParameter("username", trainerUsername);

        if (filter.getFromDate() != null) query.setParameter("fromDate", filter.getFromDate());
        if (filter.getToDate() != null) query.setParameter("toDate", filter.getToDate());
        if (filter.getTraineeName() != null && !filter.getTraineeName().isBlank()) {
            query.setParameter("traineeName", "%" + filter.getTraineeName() + "%");
        }

        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainee> findTraineesByTrainerUsername(String trainerUsername) {
        return entityManager.createQuery(
                        "SELECT trn FROM Trainer t JOIN t.trainees trn WHERE t.username = :username", Trainee.class)
                .setParameter("username", trainerUsername)
                .getResultList();
    }
}