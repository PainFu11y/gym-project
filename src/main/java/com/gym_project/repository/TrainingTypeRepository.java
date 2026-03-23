package com.gym_project.repository;

import com.gym_project.entity.TrainingType;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeRepository {

    public void save(TrainingType trainingType);

    public TrainingType update(TrainingType trainingType);

    public void delete(TrainingType trainingType);

    public Optional<TrainingType> findById(Long id);

    public List<TrainingType> findAll();

    public Optional<TrainingType> findByName(String trainingTypeName);
}