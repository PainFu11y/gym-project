package com.gym_report.messaging;

import com.gym_report.dto.TrainerWorkloadRequestDto;
import com.gym_report.service.TrainerWorkloadService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkloadMessageConsumerTest {

    @Mock
    private TrainerWorkloadService workloadService;

    @InjectMocks
    private WorkloadMessageConsumer consumer;

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    private WorkloadMessage validMessage(WorkloadMessage.ActionType action) {
        return new WorkloadMessage(
                "John.Smith",
                "John",
                "Smith",
                true,
                LocalDate.of(2025, 4, 15),
                60,
                action,
                "tx-001"
        );
    }

    // ------------------------------------------------------------------ happy path

    @Test
    void consume_shouldCallProcessWorkload_forAddAction() {
        WorkloadMessage message = validMessage(WorkloadMessage.ActionType.ADD);

        consumer.consume(message);

        ArgumentCaptor<TrainerWorkloadRequestDto> captor =
                ArgumentCaptor.forClass(TrainerWorkloadRequestDto.class);
        verify(workloadService).processWorkload(captor.capture(), eq("tx-001"));

        TrainerWorkloadRequestDto dto = captor.getValue();
        assertThat(dto.getTrainerUsername()).isEqualTo("John.Smith");
        assertThat(dto.getTrainerFirstName()).isEqualTo("John");
        assertThat(dto.getTrainerLastName()).isEqualTo("Smith");
        assertThat(dto.getIsActive()).isTrue();
        assertThat(dto.getTrainingDate()).isEqualTo(LocalDate.of(2025, 4, 15));
        assertThat(dto.getTrainingDuration()).isEqualTo(60);
        assertThat(dto.getActionType()).isEqualTo(TrainerWorkloadRequestDto.ActionType.ADD);
    }

    @Test
    void consume_shouldCallProcessWorkload_forDeleteAction() {
        WorkloadMessage message = validMessage(WorkloadMessage.ActionType.DELETE);

        consumer.consume(message);

        ArgumentCaptor<TrainerWorkloadRequestDto> captor =
                ArgumentCaptor.forClass(TrainerWorkloadRequestDto.class);
        verify(workloadService).processWorkload(captor.capture(), eq("tx-001"));

        assertThat(captor.getValue().getActionType())
                .isEqualTo(TrainerWorkloadRequestDto.ActionType.DELETE);
    }

    @Test
    void consume_shouldUseNoTxId_whenTransactionIdIsNull() {
        WorkloadMessage message = validMessage(WorkloadMessage.ActionType.ADD);
        message.setTransactionId(null);

        consumer.consume(message);

        verify(workloadService).processWorkload(any(), eq("no-tx-id"));
    }

    @Test
    void consume_shouldClearMdc_afterProcessing() {
        consumer.consume(validMessage(WorkloadMessage.ActionType.ADD));

        assertThat(MDC.get("transactionId")).isNull();
    }

    @Test
    void consume_shouldClearMdc_evenWhenValidationFails() {
        WorkloadMessage message = validMessage(WorkloadMessage.ActionType.ADD);
        message.setTrainerUsername(null);

        assertThatThrownBy(() -> consumer.consume(message))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(MDC.get("transactionId")).isNull();
    }

    // ------------------------------------------------------------------ validation

    @Test
    void consume_shouldThrow_whenTrainerUsernameIsNull() {
        WorkloadMessage message = validMessage(WorkloadMessage.ActionType.ADD);
        message.setTrainerUsername(null);

        assertThatThrownBy(() -> consumer.consume(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("trainerUsername is required");

        verifyNoInteractions(workloadService);
    }

    @Test
    void consume_shouldThrow_whenTrainerUsernameIsBlank() {
        WorkloadMessage message = validMessage(WorkloadMessage.ActionType.ADD);
        message.setTrainerUsername("   ");

        assertThatThrownBy(() -> consumer.consume(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("trainerUsername is required");

        verifyNoInteractions(workloadService);
    }

    @Test
    void consume_shouldThrow_whenTrainingDateIsNull() {
        WorkloadMessage message = validMessage(WorkloadMessage.ActionType.ADD);
        message.setTrainingDate(null);

        assertThatThrownBy(() -> consumer.consume(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("trainingDate is required");

        verifyNoInteractions(workloadService);
    }

    @Test
    void consume_shouldThrow_whenActionTypeIsNull() {
        WorkloadMessage message = validMessage(null);

        assertThatThrownBy(() -> consumer.consume(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("actionType is required");

        verifyNoInteractions(workloadService);
    }

    @Test
    void consume_shouldThrow_whenTrainingDurationIsZero() {
        WorkloadMessage message = validMessage(WorkloadMessage.ActionType.ADD);
        message.setTrainingDuration(0);

        assertThatThrownBy(() -> consumer.consume(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("trainingDuration must be positive");

        verifyNoInteractions(workloadService);
    }

    @Test
    void consume_shouldThrow_whenTrainingDurationIsNegative() {
        WorkloadMessage message = validMessage(WorkloadMessage.ActionType.ADD);
        message.setTrainingDuration(-10);

        assertThatThrownBy(() -> consumer.consume(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("trainingDuration must be positive");

        verifyNoInteractions(workloadService);
    }
}
