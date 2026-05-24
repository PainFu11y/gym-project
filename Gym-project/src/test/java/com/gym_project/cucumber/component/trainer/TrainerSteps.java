package com.gym_project.cucumber.component.trainer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym_project.controller.TrainerController;
import com.gym_project.dto.create.request.TrainerCreateRequestDto;
import com.gym_project.dto.create.response.TrainerCreateResponseDto;
import com.gym_project.dto.filter.TrainerTrainingFilterDto;
import com.gym_project.dto.response.TrainerResponseDto;
import com.gym_project.dto.response.TrainerSummaryDto;
import com.gym_project.dto.response.TrainingResponseDto;
import com.gym_project.dto.update.request.TrainerUpdateRequestDto;
import com.gym_project.dto.update.response.TrainerUpdateResponseDto;
import com.gym_project.exception.EntityNotFoundException;
import com.gym_project.exception.GlobalExceptionHandler;
import com.gym_project.service.TrainerService;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TrainerSteps {

    private TrainerService trainerService;
    private MockMvc mockMvc;
    private ResultActions result;

    private String storedFirstName;
    private String storedLastName;
    private Long storedTrainingTypeId;

    private final ObjectMapper objectMapper =
            new ObjectMapper().registerModule(new JavaTimeModule());

    @Before
    public void setUp() {
        trainerService = mock(TrainerService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TrainerController(trainerService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }


    @Given("a trainer registration with firstName {string} lastName {string} and trainingTypeId {long}")
    public void aTrainerRegistrationWith(String firstName, String lastName, Long typeId) {
        storedFirstName      = firstName;
        storedLastName       = lastName;
        storedTrainingTypeId = typeId;

        if (!firstName.isBlank() && !lastName.isBlank()) {
            TrainerCreateResponseDto response = new TrainerCreateResponseDto();
            response.setUsername(firstName + "." + lastName);
            response.setPassword("gen-pass-456");
            when(trainerService.create(any())).thenReturn(response);
        }
    }

    @Given("a trainer with username {string} exists")
    public void aTrainerExists(String username) {
        TrainerResponseDto dto = new TrainerResponseDto();
        dto.setFirstName("John");
        dto.setLastName("Smith");
        dto.setActive(true);
        when(trainerService.getByUsername(username)).thenReturn(dto);
    }

    @Given("no trainer with username {string} exists")
    public void noTrainerExists(String username) {
        when(trainerService.getByUsername(username))
                .thenThrow(new EntityNotFoundException("Trainer '" + username + "' not found"));
    }

    @Given("trainer {string} profile can be updated")
    public void trainerCanBeUpdated(String username) {
        TrainerUpdateResponseDto response = new TrainerUpdateResponseDto();
        response.setUsername(username);
        response.setFirstName("John");
        response.setLastName("Smith");
        response.setActive(true);
        when(trainerService.update(any())).thenReturn(response);
    }

    @Given("trainer {string} active status can be toggled")
    public void trainerStatusCanBeToggled(String username) {
        doNothing().when(trainerService).toggleStatus(username);
    }

    @Given("toggling trainer {string} status raises not found exception")
    public void togglingTrainerStatusRaisesNotFound(String username) {
        doThrow(new EntityNotFoundException("Trainer '" + username + "' not found"))
                .when(trainerService).toggleStatus(username);
    }

    @Given("trainee {string} has unassigned active trainers available")
    public void traineeHasUnassignedTrainers(String traineeUsername) {
        TrainerSummaryDto dto = new TrainerSummaryDto("Free.Trainer", "Free", "Trainer", "Yoga");
        when(trainerService.getUnassignedActiveTrainersByTraineeUsername(traineeUsername))
                .thenReturn(List.of(dto));
    }

    @Given("trainer {string} has training sessions")
    public void trainerHasTrainingSessions(String username) {
        when(trainerService.getTrainerTrainingsByFilter(any()))
                .thenReturn(List.of(new TrainingResponseDto()));
    }


    @When("the trainer registration request is submitted")
    public void submitTrainerRegistration() throws Exception {
        TrainerCreateRequestDto dto = new TrainerCreateRequestDto();
        dto.setFirstName(storedFirstName);
        dto.setLastName(storedLastName);
        dto.setTrainingTypeId(storedTrainingTypeId);

        result = mockMvc.perform(post("/api/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
    }

    @When("I request the profile of trainer {string}")
    public void requestTrainerProfile(String username) throws Exception {
        result = mockMvc.perform(get("/api/trainers/" + username));
    }

    @When("I update trainer {string} with firstName {string} lastName {string} and active status {word}")
    public void updateTrainer(String username, String firstName, String lastName, String active) throws Exception {
        TrainerUpdateRequestDto dto = new TrainerUpdateRequestDto();
        dto.setUsername(username);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setActive(Boolean.parseBoolean(active));

        result = mockMvc.perform(put("/api/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
    }

    @When("I update trainer with an empty username")
    public void updateTrainerWithEmptyUsername() throws Exception {
        TrainerUpdateRequestDto dto = new TrainerUpdateRequestDto();
        dto.setUsername("");
        dto.setFirstName("John");
        dto.setLastName("Smith");
        dto.setActive(true);

        result = mockMvc.perform(put("/api/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
    }

    @When("I toggle the status of trainer {string}")
    public void toggleTrainerStatus(String username) throws Exception {
        result = mockMvc.perform(patch("/api/trainers/" + username + "/status"));
    }

    @When("I request unassigned trainers for trainee {string}")
    public void requestUnassignedTrainers(String traineeUsername) throws Exception {
        result = mockMvc.perform(get("/api/trainers/unassigned/" + traineeUsername));
    }

    @When("I request filtered trainings for trainer {string}")
    public void requestFilteredTrainings(String username) throws Exception {
        TrainerTrainingFilterDto filter = new TrainerTrainingFilterDto();
        filter.setUsername(username);

        result = mockMvc.perform(post("/api/trainers/trainings/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)));
    }


    @Then("the response status is {int}")
    public void responseStatusIs(int expectedStatus) throws Exception {
        result.andExpect(status().is(expectedStatus));
    }

    @And("the response contains trainer username {string}")
    public void responseContainsTrainerUsername(String expectedUsername) throws Exception {
        result.andExpect(jsonPath("$.username").value(expectedUsername));
    }

    @And("the trainer firstName is {string}")
    public void trainerFirstNameIs(String firstName) throws Exception {
        result.andExpect(jsonPath("$.firstName").value(firstName));
    }

    @And("the trainer username in response is {string}")
    public void trainerUsernameInResponseIs(String username) throws Exception {
        result.andExpect(jsonPath("$.username").value(username));
    }

    @And("the unassigned trainer list is not empty")
    public void unassignedTrainerListIsNotEmpty() throws Exception {
        result.andExpect(jsonPath("$.length()").value(1));
    }
}
