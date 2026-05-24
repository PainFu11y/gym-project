package com.gym_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym_project.dto.create.request.TraineeCreateRequestDto;
import com.gym_project.dto.create.response.TraineeCreateResponseDto;
import com.gym_project.dto.request.TraineeTrainingsFilterRequestDto;
import com.gym_project.dto.response.TraineeResponseDto;
import com.gym_project.dto.response.TrainerSummaryDto;
import com.gym_project.dto.response.TrainingResponseDto;
import com.gym_project.dto.update.request.TraineeUpdateRequestDto;
import com.gym_project.dto.update.request.UpdateTraineeTrainerListRequestDto;
import com.gym_project.dto.common.TrainerUsernameDto;
import com.gym_project.exception.EntityNotFoundException;
import com.gym_project.exception.GlobalExceptionHandler;
import com.gym_project.service.TraineeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {

    @Mock
    private TraineeService traineeService;

    @InjectMocks
    private TraineeController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }


    @Test
    void create_shouldReturn200_withUsernameAndPassword() throws Exception {
        TraineeCreateRequestDto req = new TraineeCreateRequestDto();
        req.setFirstName("Jane");
        req.setLastName("Doe");

        TraineeCreateResponseDto resp = new TraineeCreateResponseDto();
        resp.setUsername("Jane.Doe");
        resp.setPassword("abc1234567");

        when(traineeService.create(any())).thenReturn(resp);

        mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Jane.Doe"))
                .andExpect(jsonPath("$.password").value("abc1234567"));

        verify(traineeService).create(any());
    }

    @Test
    void create_shouldReturn400_whenFirstNameIsBlank() throws Exception {
        TraineeCreateRequestDto req = new TraineeCreateRequestDto();
        req.setFirstName("");
        req.setLastName("Doe");

        mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(traineeService);
    }

    @Test
    void create_shouldReturn400_whenLastNameIsBlank() throws Exception {
        TraineeCreateRequestDto req = new TraineeCreateRequestDto();
        req.setFirstName("Jane");
        req.setLastName("");

        mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(traineeService);
    }


    @Test
    void getByUsername_shouldReturn200_withTraineeDetails() throws Exception {
        TraineeResponseDto resp = new TraineeResponseDto();
        resp.setUsername("Jane.Doe");
        resp.setFirstName("Jane");
        resp.setLastName("Doe");
        resp.setActive(true);
        resp.setTrainers(Set.of());

        when(traineeService.getByUsername("Jane.Doe")).thenReturn(resp);

        mockMvc.perform(get("/api/trainees/Jane.Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Jane.Doe"))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void getByUsername_shouldReturn404_whenTraineeNotFound() throws Exception {
        when(traineeService.getByUsername("unknown"))
                .thenThrow(new EntityNotFoundException("Trainee 'unknown' not found"));

        mockMvc.perform(get("/api/trainees/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainee 'unknown' not found"));
    }


    @Test
    void update_shouldReturn200_whenRequestIsValid() throws Exception {
        TraineeUpdateRequestDto req = new TraineeUpdateRequestDto();
        req.setUsername("Jane.Doe");
        req.setFirstName("Jane");
        req.setLastName("Doe");
        req.setActive(true);

        TraineeResponseDto resp = new TraineeResponseDto();
        resp.setUsername("Jane.Doe");
        resp.setFirstName("Jane");
        resp.setLastName("Doe");
        resp.setActive(true);
        resp.setTrainers(Set.of());

        when(traineeService.update(any())).thenReturn(resp);

        mockMvc.perform(put("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Jane.Doe"));
    }

    @Test
    void update_shouldReturn400_whenUsernameIsBlank() throws Exception {
        TraineeUpdateRequestDto req = new TraineeUpdateRequestDto();
        req.setUsername("");
        req.setFirstName("Jane");
        req.setLastName("Doe");
        req.setActive(true);

        mockMvc.perform(put("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(traineeService);
    }

    @Test
    void update_shouldReturn400_whenActiveIsNull() throws Exception {
        TraineeUpdateRequestDto req = new TraineeUpdateRequestDto();
        req.setUsername("Jane.Doe");
        req.setFirstName("Jane");
        req.setLastName("Doe");

        mockMvc.perform(put("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(traineeService);
    }


    @Test
    void delete_shouldReturn200_whenTraineeExists() throws Exception {
        doNothing().when(traineeService).deleteByUsername("Jane.Doe");

        mockMvc.perform(delete("/api/trainees/Jane.Doe"))
                .andExpect(status().isOk());

        verify(traineeService).deleteByUsername("Jane.Doe");
    }

    @Test
    void delete_shouldReturn404_whenTraineeNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Trainee 'unknown' not found"))
                .when(traineeService).deleteByUsername("unknown");

        mockMvc.perform(delete("/api/trainees/unknown"))
                .andExpect(status().isNotFound());
    }


    @Test
    void updateTrainerList_shouldReturn200_withUpdatedList() throws Exception {
        TrainerUsernameDto trainer = new TrainerUsernameDto();
        trainer.setUsername("John.Smith");

        UpdateTraineeTrainerListRequestDto req = new UpdateTraineeTrainerListRequestDto();
        req.setTraineeUsername("Jane.Doe");
        req.setTrainers(List.of(trainer));

        TrainerSummaryDto summary = new TrainerSummaryDto("John.Smith", "John", "Smith", "Yoga");
        when(traineeService.updateTrainerList(any())).thenReturn(List.of(summary));

        mockMvc.perform(put("/api/trainees/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("John.Smith"));
    }

    @Test
    void updateTrainerList_shouldReturn400_whenTraineeUsernameIsBlank() throws Exception {
        UpdateTraineeTrainerListRequestDto req = new UpdateTraineeTrainerListRequestDto();
        req.setTraineeUsername("");
        req.setTrainers(List.of());

        mockMvc.perform(put("/api/trainees/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(traineeService);
    }


    @Test
    void getTrainings_shouldReturn200_withList() throws Exception {
        TraineeTrainingsFilterRequestDto filter = new TraineeTrainingsFilterRequestDto();
        filter.setUsername("Jane.Doe");

        TrainingResponseDto training = new TrainingResponseDto();
        when(traineeService.getTraineeTrainings(any())).thenReturn(List.of(training));

        mockMvc.perform(post("/api/trainees/trainings/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getTrainings_shouldReturn400_whenUsernameIsBlank() throws Exception {
        TraineeTrainingsFilterRequestDto filter = new TraineeTrainingsFilterRequestDto();
        filter.setUsername("");

        mockMvc.perform(post("/api/trainees/trainings/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(traineeService);
    }


    @Test
    void toggleStatus_shouldReturn200() throws Exception {
        doNothing().when(traineeService).toggleStatus("Jane.Doe");

        mockMvc.perform(patch("/api/trainees/Jane.Doe/status"))
                .andExpect(status().isOk());

        verify(traineeService).toggleStatus("Jane.Doe");
    }

    @Test
    void toggleStatus_shouldReturn404_whenTraineeNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Trainee 'unknown' not found"))
                .when(traineeService).toggleStatus("unknown");

        mockMvc.perform(patch("/api/trainees/unknown/status"))
                .andExpect(status().isNotFound());
    }
}
