package com.gym_project.repository.impl;

import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.Training;
import com.gym_project.entity.TrainingType;
import com.gym_project.repository.TrainingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainingRepositoryImpl implements TrainingRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void save(Training training) {
        entityManager.persist(training);
    }

    @Override
    @Transactional
    public Training update(Training training) {
        return entityManager.merge(training);
    }

    @Override
    @Transactional
    public void delete(Training training) {
        entityManager.remove(entityManager.contains(training) ? training : entityManager.merge(training));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Training.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> findAll() {
        return entityManager.createQuery("SELECT t FROM Training t", Training.class)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> findByTrainee(Trainee trainee) {
        return entityManager.createQuery(
                        "SELECT t FROM Training t WHERE t.trainee = :trainee", Training.class)
                .setParameter("trainee", trainee)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> findByTrainer(Trainer trainer) {
        return entityManager.createQuery(
                        "SELECT t FROM Training t WHERE t.trainer = :trainer", Training.class)
                .setParameter("trainer", trainer)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> findByTrainingType(TrainingType trainingType) {
        return entityManager.createQuery(
                        "SELECT t FROM Training t WHERE t.trainingType = :type", Training.class)
                .setParameter("type", trainingType)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> findByDate(LocalDate date) {
        return entityManager.createQuery(
                        "SELECT t FROM Training t WHERE t.trainingDate = :date", Training.class)
                .setParameter("date", date)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return entityManager.createQuery(
                        "SELECT t FROM Training t WHERE t.trainingDate BETWEEN :start AND :end", Training.class)
                .setParameter("start", startDate)
                .setParameter("end", endDate)
                .getResultList();
    }
}