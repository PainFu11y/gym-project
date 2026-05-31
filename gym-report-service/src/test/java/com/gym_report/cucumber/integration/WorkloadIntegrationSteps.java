package com.gym_report.cucumber.integration;

import com.gym_report.document.TrainerWorkloadDocument;
import com.gym_report.messaging.WorkloadMessage;
import com.gym_report.messaging.WorkloadMessageConsumer;
import com.gym_report.repository.TrainerWorkloadRepository;
import com.gym_report.service.TrainerWorkloadService;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WorkloadIntegrationSteps {

    private TrainerWorkloadRepository repository;
    private TrainerWorkloadService    workloadService;
    private WorkloadMessageConsumer   consumer;

    private ArgumentCaptor<TrainerWorkloadDocument> docCaptor;
    private Throwable thrownException;

    @Before
    public void setUp() {
        repository      = mock(TrainerWorkloadRepository.class);
        workloadService = new TrainerWorkloadService(repository);
        consumer        = new WorkloadMessageConsumer(workloadService);
        docCaptor       = ArgumentCaptor.forClass(TrainerWorkloadDocument.class);
    }


    @Given("no workload record exists for trainer {string}")
    public void noWorkloadRecordExists(String username) {
        when(repository.findByUsername(username)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Given("trainer {string} already has {int} minutes in January {int}")
    public void trainerHasMinutesInJanuary(String username, int minutes, int year) {
        TrainerWorkloadDocument doc = buildDocument(username, year, 1, minutes);
        when(repository.findByUsername(username)).thenReturn(Optional.of(doc));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Given("trainer {string} already has {int} minutes in March {int}")
    public void trainerHasMinutesInMarch(String username, int minutes, int year) {
        TrainerWorkloadDocument doc = buildDocument(username, year, 3, minutes);
        when(repository.findByUsername(username)).thenReturn(Optional.of(doc));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }


    @When("the consumer receives an ADD message for trainer {string} with {int} minutes on {string}")
    public void consumerReceivesAddMessage(String username, int duration, String date) {
        WorkloadMessage message = buildMessage(username, WorkloadMessage.ActionType.ADD,
                duration, LocalDate.parse(date));
        consumer.consume(message);
    }

    @When("the consumer receives a DELETE message for trainer {string} with {int} minutes on {string}")
    public void consumerReceivesDeleteMessage(String username, int duration, String date) {
        WorkloadMessage message = buildMessage(username, WorkloadMessage.ActionType.DELETE,
                duration, LocalDate.parse(date));
        consumer.consume(message);
    }

    @When("the consumer receives a message with a blank trainer username")
    public void consumerReceivesMessageWithBlankUsername() {
        WorkloadMessage message = buildMessage("", WorkloadMessage.ActionType.ADD, 60,
                LocalDate.of(2024, 1, 15));
        thrownException = assertThrows(IllegalArgumentException.class,
                () -> consumer.consume(message));
    }

    @When("the consumer receives a message with a null training date")
    public void consumerReceivesMessageWithNullDate() {
        WorkloadMessage message = buildMessage("trainer.x", WorkloadMessage.ActionType.ADD, 60, null);
        thrownException = assertThrows(IllegalArgumentException.class,
                () -> consumer.consume(message));
    }

    @When("the consumer receives a message with duration {int}")
    public void consumerReceivesMessageWithDuration(int duration) {
        WorkloadMessage message = buildMessage("trainer.x", WorkloadMessage.ActionType.ADD, duration,
                LocalDate.of(2024, 1, 15));
        thrownException = assertThrows(IllegalArgumentException.class,
                () -> consumer.consume(message));
    }

    @When("the consumer receives a message with a null action type")
    public void consumerReceivesMessageWithNullActionType() {
        WorkloadMessage message = buildMessage("trainer.x", null, 60, LocalDate.of(2024, 1, 15));
        thrownException = assertThrows(IllegalArgumentException.class,
                () -> consumer.consume(message));
    }


    @Then("the workload repository saves a record for trainer {string}")
    public void repositorySavesRecord(String username) {
        verify(repository).save(docCaptor.capture());
        assertThat(docCaptor.getValue().getUsername()).isEqualTo(username);
    }

    @And("the saved record has {int} minutes in January {int}")
    public void savedRecordHasMinutesInJanuary(int expectedMinutes, int year) {
        TrainerWorkloadDocument saved = docCaptor.getValue();
        int actual = saved.getYears().getOrDefault(year, Map.of()).getOrDefault(1, 0);
        assertThat(actual).isEqualTo(expectedMinutes);
    }

    @And("the saved record has {int} minutes in March {int}")
    public void savedRecordHasMinutesInMarch(int expectedMinutes, int year) {
        TrainerWorkloadDocument saved = docCaptor.getValue();
        int actual = saved.getYears().getOrDefault(year, Map.of()).getOrDefault(3, 0);
        assertThat(actual).isEqualTo(expectedMinutes);
    }

    @And("the saved record has no entry for March {int}")
    public void savedRecordHasNoEntryForMarch(int year) {
        TrainerWorkloadDocument saved = docCaptor.getValue();
        Map<Integer, Integer> months = saved.getYears().getOrDefault(year, Map.of());
        assertThat(months).doesNotContainKey(3);
    }

    @Then("the message is rejected with an IllegalArgumentException")
    public void messageRejectedWithException() {
        assertThat(thrownException).isInstanceOf(IllegalArgumentException.class);
    }


    private TrainerWorkloadDocument buildDocument(String username, int year, int month, int minutes) {
        Map<Integer, Integer> months = new HashMap<>();
        months.put(month, minutes);
        Map<Integer, Map<Integer, Integer>> years = new HashMap<>();
        years.put(year, months);
        return TrainerWorkloadDocument.builder()
                .username(username)
                .firstName("John")
                .lastName("Trainer")
                .isActive(true)
                .years(years)
                .build();
    }

    private WorkloadMessage buildMessage(String username, WorkloadMessage.ActionType actionType,
                                          int duration, LocalDate date) {
        return new WorkloadMessage(username, "John", "Trainer", true, date, duration,
                actionType, "test-tx-id");
    }
}
