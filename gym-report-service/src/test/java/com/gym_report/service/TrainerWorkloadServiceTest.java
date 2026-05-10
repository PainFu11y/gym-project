package com.gym_report.service;

import com.gym_report.dto.TrainerWorkloadRequestDto;
import com.gym_report.model.TrainerWorkloadSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TrainerWorkloadServiceTest {

    private TrainerWorkloadService workloadService;

    private static final String USERNAME   = "john.doe";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME  = "Doe";
    private static final String TX_ID      = "test-tx-001";

    @BeforeEach
    void setUp() {
        workloadService = new TrainerWorkloadService();
    }


    private TrainerWorkloadRequestDto buildDto(int year, int month, int duration,
                                               TrainerWorkloadRequestDto.ActionType action) {
        TrainerWorkloadRequestDto dto = new TrainerWorkloadRequestDto();
        dto.setTrainerUsername(USERNAME);
        dto.setTrainerFirstName(FIRST_NAME);
        dto.setTrainerLastName(LAST_NAME);
        dto.setIsActive(true);
        dto.setTrainingDate(LocalDate.of(year, month, 15));
        dto.setTrainingDuration(duration);
        dto.setActionType(action);
        return dto;
    }


    @Test
    void processWorkload_ADD_shouldCreateNewEntry_whenTrainerNotExists() {
        workloadService.processWorkload(buildDto(2025, 4, 60, TrainerWorkloadRequestDto.ActionType.ADD), TX_ID);

        TrainerWorkloadSummary summary = workloadService.getWorkload(USERNAME);

        assertThat(summary).isNotNull();
        assertThat(summary.getUsername()).isEqualTo(USERNAME);
        assertThat(summary.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(summary.getLastName()).isEqualTo(LAST_NAME);
        assertThat(summary.isActive()).isTrue();
        assertThat(summary.getYears()).containsKey(2025);
        assertThat(summary.getYears().get(2025)).containsEntry(4, 60);
    }

    @Test
    void processWorkload_ADD_shouldAccumulateMinutes_forSameMonthAndYear() {
        workloadService.processWorkload(buildDto(2025, 4, 60, TrainerWorkloadRequestDto.ActionType.ADD), TX_ID);
        workloadService.processWorkload(buildDto(2025, 4, 45, TrainerWorkloadRequestDto.ActionType.ADD), TX_ID);

        TrainerWorkloadSummary summary = workloadService.getWorkload(USERNAME);

        assertThat(summary.getYears().get(2025).get(4)).isEqualTo(105);
    }

    @Test
    void processWorkload_ADD_shouldTrackMultipleMonthsIndependently() {
        workloadService.processWorkload(buildDto(2025, 4, 60, TrainerWorkloadRequestDto.ActionType.ADD), TX_ID);
        workloadService.processWorkload(buildDto(2025, 5, 90, TrainerWorkloadRequestDto.ActionType.ADD), TX_ID);

        TrainerWorkloadSummary summary = workloadService.getWorkload(USERNAME);

        assertThat(summary.getYears().get(2025)).containsEntry(4, 60);
        assertThat(summary.getYears().get(2025)).containsEntry(5, 90);
    }

    @Test
    void processWorkload_ADD_shouldTrackMultipleYearsIndependently() {
        workloadService.processWorkload(buildDto(2024, 12, 60, TrainerWorkloadRequestDto.ActionType.ADD), TX_ID);
        workloadService.processWorkload(buildDto(2025, 1,  90, TrainerWorkloadRequestDto.ActionType.ADD), TX_ID);

        TrainerWorkloadSummary summary = workloadService.getWorkload(USERNAME);

        assertThat(summary.getYears()).containsKey(2024);
        assertThat(summary.getYears()).containsKey(2025);
        assertThat(summary.getYears().get(2024).get(12)).isEqualTo(60);
        assertThat(summary.getYears().get(2025).get(1)).isEqualTo(90);
    }

    @Test
    void processWorkload_ADD_shouldUpdateTrainerPersonalInfo() {
        workloadService.processWorkload(buildDto(2025, 4, 60, TrainerWorkloadRequestDto.ActionType.ADD), TX_ID);

        TrainerWorkloadRequestDto updated = buildDto(2025, 5, 30, TrainerWorkloadRequestDto.ActionType.ADD);
        updated.setTrainerFirstName("Jonathan");
        updated.setIsActive(false);
        workloadService.processWorkload(updated, TX_ID);

        TrainerWorkloadSummary summary = workloadService.getWorkload(USERNAME);
        assertThat(summary.getFirstName()).isEqualTo("Jonathan");
        assertThat(summary.isActive()).isFalse();
    }

    @Test
    void processWorkload_DELETE_shouldSubtractMinutes() {
        workloadService.processWorkload(buildDto(2025, 4, 90, TrainerWorkloadRequestDto.ActionType.ADD), TX_ID);
        workloadService.processWorkload(buildDto(2025, 4, 30, TrainerWorkloadRequestDto.ActionType.DELETE), TX_ID);

        TrainerWorkloadSummary summary = workloadService.getWorkload(USERNAME);

        assertThat(summary.getYears().get(2025).get(4)).isEqualTo(60);
    }

    @Test
    void processWorkload_DELETE_shouldRemoveMonthEntry_whenMinutesDropToZero() {
        workloadService.processWorkload(buildDto(2025, 4, 60, TrainerWorkloadRequestDto.ActionType.ADD), TX_ID);
        workloadService.processWorkload(buildDto(2025, 4, 60, TrainerWorkloadRequestDto.ActionType.DELETE), TX_ID);

        TrainerWorkloadSummary summary = workloadService.getWorkload(USERNAME);

        assertThat(summary.getYears()).doesNotContainKey(2025);
    }

    @Test
    void processWorkload_DELETE_shouldRemoveYearEntry_whenAllMonthsAreGone() {
        workloadService.processWorkload(buildDto(2025, 3, 60, TrainerWorkloadRequestDto.ActionType.ADD), TX_ID);
        workloadService.processWorkload(buildDto(2025, 4, 90, TrainerWorkloadRequestDto.ActionType.ADD), TX_ID);
        workloadService.processWorkload(buildDto(2025, 3, 60, TrainerWorkloadRequestDto.ActionType.DELETE), TX_ID);
        workloadService.processWorkload(buildDto(2025, 4, 90, TrainerWorkloadRequestDto.ActionType.DELETE), TX_ID);

        TrainerWorkloadSummary summary = workloadService.getWorkload(USERNAME);

        assertThat(summary.getYears()).doesNotContainKey(2025);
    }

    @Test
    void processWorkload_DELETE_shouldDoNothing_whenYearDoesNotExist() {
        workloadService.processWorkload(buildDto(2099, 1, 60, TrainerWorkloadRequestDto.ActionType.DELETE), TX_ID);

        TrainerWorkloadSummary summary = workloadService.getWorkload(USERNAME);
        assertThat(summary.getYears()).doesNotContainKey(2099);
    }

    @Test
    void processWorkload_DELETE_shouldOnlyAffectTargetMonth() {
        workloadService.processWorkload(buildDto(2025, 4, 60, TrainerWorkloadRequestDto.ActionType.ADD), TX_ID);
        workloadService.processWorkload(buildDto(2025, 5, 90, TrainerWorkloadRequestDto.ActionType.ADD), TX_ID);
        workloadService.processWorkload(buildDto(2025, 4, 60, TrainerWorkloadRequestDto.ActionType.DELETE), TX_ID);

        TrainerWorkloadSummary summary = workloadService.getWorkload(USERNAME);

        assertThat(summary.getYears().get(2025)).doesNotContainKey(4);
        assertThat(summary.getYears().get(2025)).containsEntry(5, 90);
    }


    @Test
    void getWorkload_shouldReturnNull_whenTrainerNotExists() {
        assertThat(workloadService.getWorkload("nonexistent")).isNull();
    }

    @Test
    void getWorkload_shouldReturnCorrectSummary_afterMultipleOperations() {
        workloadService.processWorkload(buildDto(2025, 1, 60, TrainerWorkloadRequestDto.ActionType.ADD), TX_ID);
        workloadService.processWorkload(buildDto(2025, 2, 90, TrainerWorkloadRequestDto.ActionType.ADD), TX_ID);

        TrainerWorkloadSummary result = workloadService.getWorkload(USERNAME);

        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getYears().get(2025)).hasSize(2);
    }


    @Test
    void getAllWorkloads_shouldReturnEmptyCollection_whenNoDataExists() {
        assertThat(workloadService.getAllWorkloads()).isEmpty();
    }

    @Test
    void getAllWorkloads_shouldReturnAllTrainers() {
        TrainerWorkloadRequestDto dto1 = buildDto(2025, 4, 60, TrainerWorkloadRequestDto.ActionType.ADD);
        dto1.setTrainerUsername("trainer.one");
        workloadService.processWorkload(dto1, TX_ID);

        TrainerWorkloadRequestDto dto2 = buildDto(2025, 4, 90, TrainerWorkloadRequestDto.ActionType.ADD);
        dto2.setTrainerUsername("trainer.two");
        workloadService.processWorkload(dto2, TX_ID);

        assertThat(workloadService.getAllWorkloads()).hasSize(2);
    }
}
