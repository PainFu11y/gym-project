package com.gym_report.service;

import com.gym_report.document.TrainerWorkloadDocument;
import com.gym_report.dto.TrainerWorkloadRequestDto;
import com.gym_report.model.TrainerWorkloadSummary;
import com.gym_report.repository.TrainerWorkloadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerWorkloadService {

    private final TrainerWorkloadRepository repository;

    public void processWorkload(TrainerWorkloadRequestDto dto, String transactionId) {
        log.info("[{}] Processing workload trainer: {}, action: {}, date: {}, duration: {} min",
                transactionId,
                dto.getTrainerUsername(),
                dto.getActionType(),
                dto.getTrainingDate(),
                dto.getTrainingDuration());

        int year  = dto.getTrainingDate().getYear();
        int month = dto.getTrainingDate().getMonthValue();

        TrainerWorkloadDocument doc = repository.findByUsername(dto.getTrainerUsername())
                .orElseGet(() -> {
                    log.debug("[{}] No existing document for trainer '{}' — creating new record",
                            transactionId, dto.getTrainerUsername());

                    return TrainerWorkloadDocument.builder()
                            .username(dto.getTrainerUsername())
                            .years(new HashMap<>())
                            .build();
                });

        doc.setFirstName(dto.getTrainerFirstName());
        doc.setLastName(dto.getTrainerLastName());
        doc.setActive(dto.getIsActive());


        if (dto.getActionType() == TrainerWorkloadRequestDto.ActionType.ADD) {
            doc.addDuration(year, month, dto.getTrainingDuration());
            log.info("[{}] ADD trainer: {}, period: {}/{}, added: {} min, new total: {} min",
                    transactionId,
                    dto.getTrainerUsername(),
                    year, month,
                    dto.getTrainingDuration(),
                    doc.getYears().getOrDefault(year, new HashMap<>()).getOrDefault(month, 0));
        } else {
            doc.subtractDuration(year, month, dto.getTrainingDuration());
            log.info("[{}] DELETE trainer: {}, period: {}/{}, removed: {} min",
                    transactionId,
                    dto.getTrainerUsername(),
                    year, month,
                    dto.getTrainingDuration());
        }


        repository.save(doc);
        log.debug("[{}] Document saved for trainer '{}'", transactionId, dto.getTrainerUsername());
    }


    public TrainerWorkloadSummary getWorkload(String username) {
        log.debug("Fetching workload for trainer '{}'", username);
        return repository.findByUsername(username)
                .map(this::toSummary)
                .orElse(null);
    }


    public Collection<TrainerWorkloadSummary> getAllWorkloads() {
        log.debug("Fetching workload summaries for all trainers");
        return repository.findAll()
                .stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    private TrainerWorkloadSummary toSummary(TrainerWorkloadDocument doc) {
        return new TrainerWorkloadSummary(
                doc.getUsername(),
                doc.getFirstName(),
                doc.getLastName(),
                doc.isActive(),
                doc.getYears()
        );
    }
}
