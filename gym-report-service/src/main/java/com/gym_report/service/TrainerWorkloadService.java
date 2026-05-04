package com.gym_report.service;

import com.gym_report.dto.TrainerWorkloadRequestDto;
import com.gym_report.model.TrainerWorkloadSummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class TrainerWorkloadService {

    private final ConcurrentHashMap<String, TrainerWorkloadSummary> db = new ConcurrentHashMap<>();

    public void processWorkload(TrainerWorkloadRequestDto dto, String transactionId) {
        log.info("[{}] Processing workload for trainer: {}, action: {}",
                transactionId, dto.getTrainerUsername(), dto.getActionType());

        int year = dto.getTrainingDate().getYear();
        int month = dto.getTrainingDate().getMonthValue();

        TrainerWorkloadSummary summary = db.computeIfAbsent(
                dto.getTrainerUsername(),
                username -> new TrainerWorkloadSummary(
                        username,
                        dto.getTrainerFirstName(),
                        dto.getTrainerLastName(),
                        dto.getIsActive(),
                        new java.util.HashMap<>()
                )
        );

        summary.setFirstName(dto.getTrainerFirstName());
        summary.setLastName(dto.getTrainerLastName());
        summary.setActive(dto.getIsActive());

        if (dto.getActionType() == TrainerWorkloadRequestDto.ActionType.ADD) {
            summary.addDuration(year, month, dto.getTrainingDuration());
            log.info("[{}] Added {} minutes for {}/{} to trainer {}",
                    transactionId, dto.getTrainingDuration(), year, month, dto.getTrainerUsername());
        } else {
            summary.subtractDuration(year, month, dto.getTrainingDuration());
            log.info("[{}] Removed {} minutes for {}/{} from trainer {}",
                    transactionId, dto.getTrainingDuration(), year, month, dto.getTrainerUsername());
        }
    }

    public TrainerWorkloadSummary getWorkload(String username) {
        return db.get(username);
    }

    public Collection<TrainerWorkloadSummary> getAllWorkloads() {
        return db.values();
    }
}
