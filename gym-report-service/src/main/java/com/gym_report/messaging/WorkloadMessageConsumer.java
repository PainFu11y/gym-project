package com.gym_report.messaging;

import com.gym_report.dto.TrainerWorkloadRequestDto;
import com.gym_report.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Profile("!standalone")
@RequiredArgsConstructor
@Slf4j
public class WorkloadMessageConsumer {

    private final TrainerWorkloadService workloadService;

    @JmsListener(destination = "${app.jms.workload-queue:trainer.workload.queue}",
                 containerFactory = "jmsListenerContainerFactory")
    public void consume(WorkloadMessage message) {
        String txId = message.getTransactionId() != null
                ? message.getTransactionId()
                : "no-tx-id";

        MDC.put("transactionId", txId);
        try {
            log.info("[{}] Received workload message: trainer={}, action={}",
                    txId, message.getTrainerUsername(), message.getActionType());

            validate(message, txId);

            TrainerWorkloadRequestDto dto = toDto(message);
            workloadService.processWorkload(dto, txId);

            log.info("[{}] Workload message processed successfully for trainer={}",
                    txId, message.getTrainerUsername());

        } finally {
            MDC.remove("transactionId");
        }
    }

    private void validate(WorkloadMessage message, String txId) {
        if (message.getTrainerUsername() == null || message.getTrainerUsername().isBlank()) {
            log.error("[{}] Invalid message: trainerUsername is missing. Sending to DLQ.", txId);
            throw new IllegalArgumentException("trainerUsername is required");
        }
        if (message.getTrainingDate() == null) {
            log.error("[{}] Invalid message: trainingDate is missing. Sending to DLQ.", txId);
            throw new IllegalArgumentException("trainingDate is required");
        }
        if (message.getActionType() == null) {
            log.error("[{}] Invalid message: actionType is missing. Sending to DLQ.", txId);
            throw new IllegalArgumentException("actionType is required");
        }
        if (message.getTrainingDuration() <= 0) {
            log.error("[{}] Invalid message: trainingDuration must be positive. Sending to DLQ.", txId);
            throw new IllegalArgumentException("trainingDuration must be positive");
        }
    }

    private TrainerWorkloadRequestDto toDto(WorkloadMessage message) {
        TrainerWorkloadRequestDto dto = new TrainerWorkloadRequestDto();
        dto.setTrainerUsername(message.getTrainerUsername());
        dto.setTrainerFirstName(message.getTrainerFirstName());
        dto.setTrainerLastName(message.getTrainerLastName());
        dto.setIsActive(message.isActive());
        dto.setTrainingDate(message.getTrainingDate());
        dto.setTrainingDuration(message.getTrainingDuration());
        dto.setActionType(message.getActionType() == WorkloadMessage.ActionType.ADD
                ? TrainerWorkloadRequestDto.ActionType.ADD
                : TrainerWorkloadRequestDto.ActionType.DELETE);
        return dto;
    }
}
