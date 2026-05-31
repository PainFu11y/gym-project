package com.gym_report.cucumber.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym_report.controller.TrainerWorkloadController;
import com.gym_report.dto.TrainerWorkloadRequestDto;
import com.gym_report.model.TrainerWorkloadSummary;
import com.gym_report.service.TrainerWorkloadService;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class WorkloadSteps {

    private TrainerWorkloadService workloadService;
    private MockMvc mockMvc;
    private ResultActions result;
    private String capturedTransactionId;

    private final ObjectMapper objectMapper =
            new ObjectMapper().registerModule(new JavaTimeModule());

    @Before
    public void setUp() {
        workloadService = mock(TrainerWorkloadService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TrainerWorkloadController(workloadService))
                .build();
    }


    @Given("the workload service processes updates without error")
    public void workloadServiceProcessesWithoutError() {
        doNothing().when(workloadService).processWorkload(any(), any());
    }

    @Given("trainer {string} has workload of {int} minutes in April {int}")
    public void trainerHasWorkloadInApril(String username, int minutes, int year) {
        Map<Integer, Integer> months = new HashMap<>();
        months.put(4, minutes);
        Map<Integer, Map<Integer, Integer>> years = new HashMap<>();
        years.put(year, months);
        TrainerWorkloadSummary summary =
                new TrainerWorkloadSummary(username, "John", "Doe", true, years);
        when(workloadService.getWorkload(username)).thenReturn(summary);
    }

    @Given("no workload data exists for trainer {string}")
    public void noWorkloadDataExists(String username) {
        when(workloadService.getWorkload(username)).thenReturn(null);
    }


    @When("I send a POST request to update workload with trainer {string} action {word} duration {int} on {string}")
    public void sendWorkloadUpdateRequest(String username, String action, int duration, String date) throws Exception {
        String body = buildValidBody(username, action, duration, date);
        result = mockMvc.perform(post("/api/trainer-workload")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    @When("I send a POST request with transaction ID {string} for trainer {string}")
    public void sendWithTransactionId(String txId, String username) throws Exception {
        capturedTransactionId = txId;
        String body = buildValidBody(username, "ADD", 60, "2025-04-15");
        result = mockMvc.perform(post("/api/trainer-workload")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Transaction-Id", txId)
                .content(body));
    }

    @When("I send a POST request with missing {word}")
    public void sendRequestWithMissingField(String missingField) throws Exception {
        String body = buildBodyWithMissingField(missingField);
        result = mockMvc.perform(post("/api/trainer-workload")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    @When("I send a POST request with an empty body")
    public void sendEmptyBody() throws Exception {
        result = mockMvc.perform(post("/api/trainer-workload")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"));
    }

    @When("I request workload for trainer {string}")
    public void requestWorkloadForTrainer(String username) throws Exception {
        result = mockMvc.perform(get("/api/trainer-workload/" + username));
    }


    @Then("the workload response status is {int}")
    public void workloadResponseStatusIs(int expectedStatus) throws Exception {
        result.andExpect(status().is(expectedStatus));
    }

    @And("the workload service was called with transaction ID {string}")
    public void workloadServiceCalledWithTransactionId(String txId) {
        verify(workloadService).processWorkload(any(), eq(txId));
    }

    @And("the workload summary contains trainer username {string}")
    public void workloadSummaryContainsUsername(String username) throws Exception {
        result.andExpect(jsonPath("$.username").value(username));
    }

    @And("the workload summary contains {int} minutes in April {int}")
    public void workloadSummaryContainsMinutesInApril(int minutes, int year) throws Exception {
        result.andExpect(jsonPath("$.years['" + year + "']['4']").value(minutes));
    }


    private String buildValidBody(String username, String action, int duration, String date) {
        return """
                {
                  "trainerUsername":  "%s",
                  "trainerFirstName": "John",
                  "trainerLastName":  "Doe",
                  "isActive":         true,
                  "trainingDate":     "%s",
                  "trainingDuration": %d,
                  "actionType":       "%s"
                }
                """.formatted(username, date, duration, action);
    }

    private String buildBodyWithMissingField(String missingField) {
        return switch (missingField) {
            case "trainerUsername" -> """
                    {
                      "trainerFirstName": "John",
                      "trainerLastName":  "Doe",
                      "isActive":         true,
                      "trainingDate":     "2025-04-15",
                      "trainingDuration": 90,
                      "actionType":       "ADD"
                    }
                    """;
            case "trainingDate" -> """
                    {
                      "trainerUsername":  "john.doe",
                      "trainerFirstName": "John",
                      "trainerLastName":  "Doe",
                      "isActive":         true,
                      "trainingDuration": 90,
                      "actionType":       "ADD"
                    }
                    """;
            case "actionType" -> """
                    {
                      "trainerUsername":  "john.doe",
                      "trainerFirstName": "John",
                      "trainerLastName":  "Doe",
                      "isActive":         true,
                      "trainingDate":     "2025-04-15",
                      "trainingDuration": 90
                    }
                    """;
            case "trainingDuration" -> """
                    {
                      "trainerUsername":  "john.doe",
                      "trainerFirstName": "John",
                      "trainerLastName":  "Doe",
                      "isActive":         true,
                      "trainingDate":     "2025-04-15",
                      "actionType":       "ADD"
                    }
                    """;
            default -> "{}";
        };
    }
}
