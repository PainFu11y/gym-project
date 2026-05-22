package com.gym_report.repository;

import com.gym_report.document.TrainerWorkloadDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TrainerWorkloadRepository
        extends MongoRepository<TrainerWorkloadDocument, String> {

    Optional<TrainerWorkloadDocument> findByUsername(String username);
}
