package com.gym_project.cucumber.component.training;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym_project.controller.TrainingController;
import com.gym_project.dto.create.request.TrainingCreateRequestDto;
import com.gym_project.exception.EntityNotFoundException;
import com.gym_project.exception.GlobalExceptionHandler;
import com.gym_project.service.TrainingService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TrainingSteps {

    private TrainingService trainingService;
    private MockMvc mockMvc;
    private ResultActions result;

    private final ObjectMapper objectMapper =
            new ObjectMapper().registerModule(new JavaTimeModule());

    @Before
    public void setUp() {
        trainingService = mock(TrainingService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TrainingController(trainingService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }


    @Given("the training service accepts new sessions")
    public void trainingServiceAcceptsNewSessions() {
        doNothing().when(trainingService).create(any());
    }

    @Given("a training session with id {long} exists")
    public void trainingSessionExists(Long id) {
        doNothing().when(trainingService).delete(id);
    }

    @Given("no training session with id {long} exists")
    public void noTrainingSessionExists(Long id) {
        doThrow(new EntityNotFoundException("Training not found: " + id))
                .when(trainingService).delete(id);
    }


    @When("I create a training with trainee {string} trainer {string} name {string} on {string} for {int} minutes")
    public void createTraining(String trainee, String trainer, String name, String date, int duration) throws Exception {
        TrainingCreateRequestDto dto = new TrainingCreateRequestDto();
        dto.setTraineeUsername(trainee);
        dto.setTrainerUsername(trainer);
        dto.setTrainingName(name);
        dto.setTrainingDate(date.isBlank() ? null : LocalDate.parse(date));
        dto.setTrainingDuration(duration);

        result = mockMvc.perform(post("/api/trainings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
    }

    @When("I delete training session with id {long}")
    public void deleteTrainingSession(Long id) throws Exception {
        result = mockMvc.perform(delete("/api/trainings/" + id));
    }


    @Then("the response status is {int}")
    public void responseStatusIs(int expectedStatus) throws Exception {
        result.andExpect(status().is(expectedStatus));
    }
}
