package com.gym_project.repository.impl;

import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.exception.EntityNotFoundException;
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
    public List<Trainer> findAll() {
        return entityManager.createQuery("SELECT t FROM Trainer t", Trainer.class)
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
    public List<Trainer> findUnassignedActiveTrainersByTraineeUsername(String traineeUsername) {
        return entityManager.createQuery(
                        "SELECT tr FROM Trainer tr " +
                                "WHERE tr.isActive = true " +
                                "AND NOT EXISTS (" +
                                "   SELECT t FROM Trainee t JOIN t.trainers trn " +
                                "   WHERE t.username = :username AND trn = tr" +
                                ")",
                        Trainer.class)
                .setParameter("username", traineeUsername)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findUsernamesStartingWith(String prefix) {
        return entityManager.createQuery(
                        "SELECT u.username FROM User u WHERE u.username LIKE :prefix",
                        String.class)
                .setParameter("prefix", prefix + "%")
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainee> findTraineesByTrainerUsername(String trainerUsername) {
        return entityManager.createQuery(
                        "SELECT trn FROM Trainer t JOIN t.trainees trn WHERE t.username = :username", Trainee.class)
                .setParameter("username", trainerUsername)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> findTrainersByUsernames(List<String> usernames) {
        return entityManager.createQuery(
                        "SELECT tr FROM Trainer tr WHERE tr.username IN :usernames", Trainer.class)
                .setParameter("usernames", usernames)
                .getResultList();
    }

    @Override
    @Transactional
    public void toggleStatus(String username) {
        Trainer trainer = entityManager.createQuery(
                        "SELECT t FROM Trainer t WHERE t.username = :username", Trainer.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + username));

        trainer.setActive(!trainer.isActive());
        entityManager.merge(trainer);
    }
}