package com.gym_project.repository.impl;

import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.exception.EntityNotFoundException;
import com.gym_project.repository.TraineeRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
                        "SELECT u.username FROM User u WHERE u.username LIKE :prefix",
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

    @Override
    @Transactional
    public void toggleStatus(String username) {

        Trainee trainee = entityManager.createQuery(
                        "SELECT t FROM Trainee t WHERE t.username = :username", Trainee.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + username));

        trainee.setActive(!trainee.isActive());

        entityManager.merge(trainee);
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