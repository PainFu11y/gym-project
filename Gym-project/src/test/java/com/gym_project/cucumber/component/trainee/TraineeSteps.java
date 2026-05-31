package com.gym_project.cucumber.component.trainee;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym_project.controller.TraineeController;
import com.gym_project.dto.common.TrainerUsernameDto;
import com.gym_project.dto.create.request.TraineeCreateRequestDto;
import com.gym_project.dto.create.response.TraineeCreateResponseDto;
import com.gym_project.dto.request.TraineeTrainingsFilterRequestDto;
import com.gym_project.dto.response.TraineeResponseDto;
import com.gym_project.dto.response.TrainerSummaryDto;
import com.gym_project.dto.response.TrainingResponseDto;
import com.gym_project.dto.update.request.TraineeUpdateRequestDto;
import com.gym_project.dto.update.request.UpdateTraineeTrainerListRequestDto;
import com.gym_project.exception.EntityNotFoundException;
import com.gym_project.exception.GlobalExceptionHandler;
import com.gym_project.service.TraineeService;
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
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TraineeSteps {

    private TraineeService traineeService;
    private MockMvc mockMvc;
    private ResultActions result;


    private String pendingFirstName = "";
    private String pendingLastName  = "";

    private final ObjectMapper objectMapper =
            new ObjectMapper().registerModule(new JavaTimeModule());

    @Before
    public void setUp() {
        traineeService = mock(TraineeService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TraineeController(traineeService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }


    @Given("a trainee registration with firstName {string} and lastName {string}")
    public void aTraineeRegistrationWith(String firstName, String lastName) {
        pendingFirstName = firstName;
        pendingLastName  = lastName;

        if (!firstName.isBlank() && !lastName.isBlank()) {
            TraineeCreateResponseDto response = new TraineeCreateResponseDto();
            response.setUsername(firstName + "." + lastName);
            response.setPassword("gen-pass-123");
            when(traineeService.create(any())).thenReturn(response);
        }
    }

    @Given("a trainee with username {string} exists")
    public void aTraineeWithUsernameExists(String username) {
        TraineeResponseDto dto = new TraineeResponseDto();
        dto.setUsername(username);
        dto.setFirstName("Jane");
        dto.setLastName("Doe");
        dto.setActive(true);
        dto.setTrainers(Set.of());
        when(traineeService.getByUsername(username)).thenReturn(dto);
    }

    @Given("no trainee with username {string} exists")
    public void noTraineeWithUsernameExists(String username) {
        when(traineeService.getByUsername(username))
                .thenThrow(new EntityNotFoundException("Trainee '" + username + "' not found"));
    }

    @Given("trainee {string} profile can be updated successfully")
    public void traineeProfileCanBeUpdated(String username) {
        TraineeResponseDto response = new TraineeResponseDto();
        response.setUsername(username);
        response.setFirstName("Jane");
        response.setLastName("Doe");
        response.setActive(true);
        response.setTrainers(Set.of());
        when(traineeService.update(any())).thenReturn(response);
    }

    @Given("trainee {string} can be deleted")
    public void traineeCanBeDeleted(String username) {
        doNothing().when(traineeService).deleteByUsername(username);
    }

    @Given("deleting trainee {string} raises not found exception")
    public void deletingTraineeRaisesNotFound(String username) {
        doThrow(new EntityNotFoundException("Trainee '" + username + "' not found"))
                .when(traineeService).deleteByUsername(username);
    }

    @Given("trainee {string} active status can be toggled")
    public void traineeStatusCanBeToggled(String username) {
        doNothing().when(traineeService).toggleStatus(username);
    }

    @Given("toggling status of {string} raises not found exception")
    public void togglingStatusRaisesNotFound(String username) {
        doThrow(new EntityNotFoundException("Trainee '" + username + "' not found"))
                .when(traineeService).toggleStatus(username);
    }

    @Given("trainee {string} has one training session")
    public void traineeHasOneTrainingSession(String username) {
        when(traineeService.getTraineeTrainings(any()))
                .thenReturn(List.of(new TrainingResponseDto()));
    }

    @Given("trainee {string} trainer list can be updated with {string}")
    public void traineeTrainerListCanBeUpdated(String traineeUsername, String trainerUsername) {
        TrainerSummaryDto summary =
                new TrainerSummaryDto(trainerUsername, "John", "Smith", "Yoga");
        when(traineeService.updateTrainerList(any())).thenReturn(List.of(summary));
    }


    @When("the trainee registration request is submitted")
    public void submitTraineeRegistration() throws Exception {
        TraineeCreateRequestDto dto = new TraineeCreateRequestDto();
        dto.setFirstName(pendingFirstName);
        dto.setLastName(pendingLastName);

        result = mockMvc.perform(post("/api/trainees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
    }

    @When("I request the profile of trainee {string}")
    public void requestTraineeProfile(String username) throws Exception {
        result = mockMvc.perform(get("/api/trainees/" + username));
    }

    @When("I update trainee {string} with firstName {string} lastName {string} and active status {word}")
    public void updateTrainee(String username, String firstName, String lastName, String active) throws Exception {
        TraineeUpdateRequestDto dto = new TraineeUpdateRequestDto();
        dto.setUsername(username);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setActive(Boolean.parseBoolean(active));

        result = mockMvc.perform(put("/api/trainees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
    }

    @When("I update trainee with an empty username")
    public void updateTraineeWithEmptyUsername() throws Exception {
        TraineeUpdateRequestDto dto = new TraineeUpdateRequestDto();
        dto.setUsername("");
        dto.setFirstName("Jane");
        dto.setLastName("Doe");
        dto.setActive(true);

        result = mockMvc.perform(put("/api/trainees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
    }

    @When("I delete trainee {string}")
    public void deleteTrainee(String username) throws Exception {
        result = mockMvc.perform(delete("/api/trainees/" + username));
    }

    @When("I toggle the status of trainee {string}")
    public void toggleTraineeStatus(String username) throws Exception {
        result = mockMvc.perform(patch("/api/trainees/" + username + "/status"));
    }

    @When("I request filtered trainings for trainee {string}")
    public void requestFilteredTrainings(String username) throws Exception {
        TraineeTrainingsFilterRequestDto filter = new TraineeTrainingsFilterRequestDto();
        filter.setUsername(username);

        result = mockMvc.perform(post("/api/trainees/trainings/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)));
    }

    @When("I request filtered trainings with a blank username")
    public void requestFilteredTrainingsWithBlankUsername() throws Exception {
        TraineeTrainingsFilterRequestDto filter = new TraineeTrainingsFilterRequestDto();
        filter.setUsername("");

        result = mockMvc.perform(post("/api/trainees/trainings/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)));
    }

    @When("I update trainer list for trainee {string} with trainer {string}")
    public void updateTrainerList(String traineeUsername, String trainerUsername) throws Exception {
        TrainerUsernameDto trainerDto = new TrainerUsernameDto();
        trainerDto.setUsername(trainerUsername);

        UpdateTraineeTrainerListRequestDto dto = new UpdateTraineeTrainerListRequestDto();
        dto.setTraineeUsername(traineeUsername);
        dto.setTrainers(List.of(trainerDto));

        result = mockMvc.perform(put("/api/trainees/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
    }

    @When("I update trainer list with a blank trainee username")
    public void updateTrainerListWithBlankUsername() throws Exception {
        TrainerUsernameDto trainerDto = new TrainerUsernameDto();
        trainerDto.setUsername("John.Smith");

        UpdateTraineeTrainerListRequestDto dto = new UpdateTraineeTrainerListRequestDto();
        dto.setTraineeUsername("");
        dto.setTrainers(List.of(trainerDto));

        result = mockMvc.perform(put("/api/trainees/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
    }


    @Then("the response status is {int}")
    public void responseStatusIs(int expectedStatus) throws Exception {
        result.andExpect(status().is(expectedStatus));
    }

    @And("the response contains username {string} and a generated password")
    public void responseContainsUsernameAndPassword(String expectedUsername) throws Exception {
        result.andExpect(jsonPath("$.username").value(expectedUsername))
              .andExpect(jsonPath("$.password").exists());
    }

    @And("the trainee firstName is {string}")
    public void traineeFirstNameIs(String firstName) throws Exception {
        result.andExpect(jsonPath("$.firstName").value(firstName));
    }

    @And("the trainee username in response is {string}")
    public void traineeUsernameInResponseIs(String username) throws Exception {
        result.andExpect(jsonPath("$.username").value(username));
    }

    @And("the training list contains {int} entry")
    public void trainingListContainsEntries(int count) throws Exception {
        result.andExpect(jsonPath("$.length()").value(count));
    }

    @And("the trainer list contains trainer {string}")
    public void trainerListContainsTrainer(String trainerUsername) throws Exception {
        result.andExpect(jsonPath("$[0].username").value(trainerUsername));
    }
}
