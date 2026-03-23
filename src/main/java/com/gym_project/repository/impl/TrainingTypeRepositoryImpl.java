package com.gym_project.repository.impl;

import com.gym_project.entity.TrainingType;
import com.gym_project.repository.TrainingRepository;
import com.gym_project.repository.TrainingTypeRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainingTypeRepositoryImpl implements TrainingTypeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(TrainingType trainingType) {
        entityManager.persist(trainingType);
    }

    @Transactional
    public TrainingType update(TrainingType trainingType) {
        return entityManager.merge(trainingType);
    }

    @Transactional
    public void delete(TrainingType trainingType) {
        entityManager.remove(entityManager.contains(trainingType) ? trainingType : entityManager.merge(trainingType));
    }

    @Transactional(readOnly = true)
    public Optional<TrainingType> findById(Long id) {
        return Optional.ofNullable(entityManager.find(TrainingType.class, id));
    }

    @Transactional(readOnly = true)
    public List<TrainingType> findAll() {
        return entityManager.createQuery("SELECT t FROM TrainingType t", TrainingType.class)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public Optional<TrainingType> findByName(String trainingTypeName) {
        return entityManager.createQuery(
                        "SELECT t FROM TrainingType t WHERE t.trainingTypeName = :name", TrainingType.class)
                .setParameter("name", trainingTypeName)
                .getResultStream()
                .findFirst();
    }
}