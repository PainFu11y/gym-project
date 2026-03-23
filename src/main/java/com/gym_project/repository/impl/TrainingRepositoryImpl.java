package com.gym_project.repository.impl;

import com.gym_project.dto.request.TraineeTrainingsFilterRequestDto;
import com.gym_project.dto.request.TrainerTrainingsRequestDto;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.Training;
import com.gym_project.repository.TrainingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

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
    @Transactional(readOnly = true)
    public List<Training> findByTraineeFilter(TraineeTrainingsFilterRequestDto filter) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Training> query = cb.createQuery(Training.class);

        Root<Training> training = query.from(Training.class);
        Join<Object, Object> trainer = training.join("trainer");
        Join<Object, Object> trainingType = training.join("trainingType");
        Join<Object, Object> trainee = training.join("trainee");

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(trainee.get("username"), filter.getUsername()));

        if (filter.getPeriodFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(
                    training.get("trainingDate"),
                    filter.getPeriodFrom()
            ));
        }

        if (filter.getPeriodTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(
                    training.get("trainingDate"),
                    filter.getPeriodTo()
            ));
        }

        if (filter.getTrainerName() != null && !filter.getTrainerName().isBlank()) {
            predicates.add(cb.like(
                    cb.lower(trainer.get("firstName")),
                    "%" + filter.getTrainerName().toLowerCase() + "%"
            ));
        }

        if (filter.getTrainingType() != null && !filter.getTrainingType().isBlank()) {
            predicates.add(cb.equal(
                    trainingType.get("trainingTypeName"),
                    filter.getTrainingType()
            ));
        }

        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> findByTrainerFilter(TrainerTrainingsRequestDto filter) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Training> query = cb.createQuery(Training.class);
        Root<Training> training = query.from(Training.class);

        Join<Training, Trainee> trainee = training.join("trainee", JoinType.INNER);
        Join<Training, Trainer> trainer = training.join("trainer", JoinType.INNER);

        List<Predicate> predicates = new ArrayList<>();

        if (filter.getUsername() != null && !filter.getUsername().isBlank()) {
            predicates.add(cb.equal(trainer.get("username"), filter.getUsername()));
        }

        if (filter.getPeriodFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(training.get("trainingDate"), filter.getPeriodFrom()));
        }
        if (filter.getPeriodTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(training.get("trainingDate"), filter.getPeriodTo()));
        }

        if (filter.getTraineeName() != null && !filter.getTraineeName().isBlank()) {
            Predicate firstNameMatch = cb.like(cb.lower(trainee.get("firstName")), "%" + filter.getTraineeName().toLowerCase() + "%");
            Predicate lastNameMatch = cb.like(cb.lower(trainee.get("lastName")), "%" + filter.getTraineeName().toLowerCase() + "%");
            predicates.add(cb.or(firstNameMatch, lastNameMatch));
        }

        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getResultList();
    }
}