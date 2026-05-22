package com.gym_report.service;

import com.gym_report.document.TrainerWorkloadDocument;
import com.gym_report.dto.TrainerWorkloadRequestDto;
import com.gym_report.model.TrainerWorkloadSummary;
import com.gym_report.repository.TrainerWorkloadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceTest {

    @Mock
    private TrainerWorkloadRepository repository;

    @InjectMocks
    private TrainerWorkloadService service;

    private static final String USERNAME   = "john.doe";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME  = "Doe";
    private static final String TX_ID      = "test-tx-001";

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

    private TrainerWorkloadDocument existingDoc(int year, int month, int currentMinutes) {
        Map<Integer, Integer> months = new HashMap<>();
        months.put(month, currentMinutes);
        Map<Integer, Map<Integer, Integer>> years = new HashMap<>();
        years.put(year, months);
        return TrainerWorkloadDocument.builder()
                .id("doc-id-1")
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .isActive(true)
                .years(years)
                .build();
    }

    // ------------------------------------------------------------------ ADD — new trainer

    @Test
    void processWorkload_ADD_shouldCreateNewDocument_whenTrainerNotFound() {
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.processWorkload(buildDto(2025, 4, 60, TrainerWorkloadRequestDto.ActionType.ADD), TX_ID);

        ArgumentCaptor<TrainerWorkloadDocument> captor = ArgumentCaptor.forClass(TrainerWorkloadDocument.class);
        verify(repository).save(captor.capture());

        TrainerWorkloadDocument saved = captor.getValue();
        assertThat(saved.getUsername()).isEqualTo(USERNAME);
        assertThat(saved.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(saved.getLastName()).isEqualTo(LAST_NAME);
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getYears()).containsKey(2025);
        assertThat(saved.getYears().get(2025)).containsEntry(4, 60);
    }

    @Test
    void processWorkload_ADD_shouldAccumulateMinutes_onExistingDocument() {
        TrainerWorkloadDocument doc = existingDoc(2025, 4, 60);
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(doc));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.processWorkload(buildDto(2025, 4, 45, TrainerWorkloadRequestDto.ActionType.ADD), TX_ID);

        ArgumentCaptor<TrainerWorkloadDocument> captor = ArgumentCaptor.forClass(TrainerWorkloadDocument.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue().getYears().get(2025).get(4)).isEqualTo(105);
    }

    @Test
    void processWorkload_ADD_shouldTrackMultipleMonthsIndependently() {
        TrainerWorkloadDocument doc = existingDoc(2025, 4, 60);
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(doc));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.processWorkload(buildDto(2025, 5, 90, TrainerWorkloadRequestDto.ActionType.ADD), TX_ID);

        ArgumentCaptor<TrainerWorkloadDocument> captor = ArgumentCaptor.forClass(TrainerWorkloadDocument.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue().getYears().get(2025)).containsEntry(4, 60);
        assertThat(captor.getValue().getYears().get(2025)).containsEntry(5, 90);
    }

    @Test
    void processWorkload_ADD_shouldTrackMultipleYearsIndependently() {
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.processWorkload(buildDto(2024, 12, 60, TrainerWorkloadRequestDto.ActionType.ADD), TX_ID);

        ArgumentCaptor<TrainerWorkloadDocument> captor = ArgumentCaptor.forClass(TrainerWorkloadDocument.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getYears().get(2024).get(12)).isEqualTo(60);
    }

    @Test
    void processWorkload_ADD_shouldUpdateTrainerPersonalInfo() {
        TrainerWorkloadDocument doc = existingDoc(2025, 4, 60);
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(doc));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TrainerWorkloadRequestDto updated = buildDto(2025, 5, 30, TrainerWorkloadRequestDto.ActionType.ADD);
        updated.setTrainerFirstName("Jonathan");
        updated.setIsActive(false);

        service.processWorkload(updated, TX_ID);

        ArgumentCaptor<TrainerWorkloadDocument> captor = ArgumentCaptor.forClass(TrainerWorkloadDocument.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue().getFirstName()).isEqualTo("Jonathan");
        assertThat(captor.getValue().isActive()).isFalse();
    }

    // ------------------------------------------------------------------ DELETE

    @Test
    void processWorkload_DELETE_shouldSubtractMinutes() {
        TrainerWorkloadDocument doc = existingDoc(2025, 4, 90);
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(doc));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.processWorkload(buildDto(2025, 4, 30, TrainerWorkloadRequestDto.ActionType.DELETE), TX_ID);

        ArgumentCaptor<TrainerWorkloadDocument> captor = ArgumentCaptor.forClass(TrainerWorkloadDocument.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue().getYears().get(2025).get(4)).isEqualTo(60);
    }

    @Test
    void processWorkload_DELETE_shouldRemoveMonthEntry_whenMinutesDropToZero() {
        TrainerWorkloadDocument doc = existingDoc(2025, 4, 60);
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(doc));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.processWorkload(buildDto(2025, 4, 60, TrainerWorkloadRequestDto.ActionType.DELETE), TX_ID);

        ArgumentCaptor<TrainerWorkloadDocument> captor = ArgumentCaptor.forClass(TrainerWorkloadDocument.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue().getYears()).doesNotContainKey(2025);
    }

    @Test
    void processWorkload_DELETE_shouldDoNothing_whenYearDoesNotExist() {
        TrainerWorkloadDocument doc = existingDoc(2025, 4, 60);
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(doc));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.processWorkload(buildDto(2099, 1, 60, TrainerWorkloadRequestDto.ActionType.DELETE), TX_ID);

        ArgumentCaptor<TrainerWorkloadDocument> captor = ArgumentCaptor.forClass(TrainerWorkloadDocument.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue().getYears()).doesNotContainKey(2099);
        assertThat(captor.getValue().getYears()).containsKey(2025);   // unchanged
    }

    @Test
    void processWorkload_DELETE_shouldOnlyAffectTargetMonth() {
        // Pre-build a doc with two months
        Map<Integer, Integer> months = new HashMap<>();
        months.put(4, 60);
        months.put(5, 90);
        Map<Integer, Map<Integer, Integer>> years = new HashMap<>();
        years.put(2025, months);
        TrainerWorkloadDocument doc = TrainerWorkloadDocument.builder()
                .id("doc-id-1").username(USERNAME)
                .firstName(FIRST_NAME).lastName(LAST_NAME).isActive(true)
                .years(years).build();

        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(doc));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.processWorkload(buildDto(2025, 4, 60, TrainerWorkloadRequestDto.ActionType.DELETE), TX_ID);

        ArgumentCaptor<TrainerWorkloadDocument> captor = ArgumentCaptor.forClass(TrainerWorkloadDocument.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue().getYears().get(2025)).doesNotContainKey(4);
        assertThat(captor.getValue().getYears().get(2025)).containsEntry(5, 90);
    }

    // ------------------------------------------------------------------ repository interaction

    @Test
    void processWorkload_shouldCallFindByUsername_once() {
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.processWorkload(buildDto(2025, 4, 60, TrainerWorkloadRequestDto.ActionType.ADD), TX_ID);

        verify(repository, times(1)).findByUsername(USERNAME);
        verify(repository, times(1)).save(any());
    }

    // ------------------------------------------------------------------ getWorkload

    @Test
    void getWorkload_shouldReturnNull_whenTrainerNotFound() {
        when(repository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThat(service.getWorkload("unknown")).isNull();
    }

    @Test
    void getWorkload_shouldReturnSummary_whenTrainerExists() {
        TrainerWorkloadDocument doc = existingDoc(2025, 4, 90);
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(doc));

        TrainerWorkloadSummary result = service.getWorkload(USERNAME);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(result.getYears().get(2025)).containsEntry(4, 90);
    }

    @Test
    void getWorkload_shouldMapAllFieldsCorrectly() {
        TrainerWorkloadDocument doc = existingDoc(2025, 6, 120);
        doc.setActive(false);
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(doc));

        TrainerWorkloadSummary result = service.getWorkload(USERNAME);

        assertThat(result.getLastName()).isEqualTo(LAST_NAME);
        assertThat(result.isActive()).isFalse();
    }

    // ------------------------------------------------------------------ getAllWorkloads

    @Test
    void getAllWorkloads_shouldReturnEmptyCollection_whenNoDocumentsExist() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.getAllWorkloads()).isEmpty();
    }

    @Test
    void getAllWorkloads_shouldReturnAllTrainers() {
        TrainerWorkloadDocument doc1 = existingDoc(2025, 4, 60);
        doc1.setUsername("trainer.one");

        TrainerWorkloadDocument doc2 = existingDoc(2025, 5, 90);
        doc2.setUsername("trainer.two");

        when(repository.findAll()).thenReturn(List.of(doc1, doc2));

        assertThat(service.getAllWorkloads()).hasSize(2);
    }
}
