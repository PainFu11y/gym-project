package com.gym_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TrainerController controller;

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
        TrainerCreateRequestDto req = new TrainerCreateRequestDto();
        req.setFirstName("John");
        req.setLastName("Smith");
        req.setTrainingTypeId(1L);

        TrainerCreateResponseDto resp = new TrainerCreateResponseDto();
        resp.setUsername("John.Smith");
        resp.setPassword("xP9kL2mN8q");

        when(trainerService.create(any())).thenReturn(resp);

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("John.Smith"))
                .andExpect(jsonPath("$.password").value("xP9kL2mN8q"));

        verify(trainerService).create(any());
    }

    @Test
    void create_shouldReturn400_whenFirstNameIsBlank() throws Exception {
        TrainerCreateRequestDto req = new TrainerCreateRequestDto();
        req.setFirstName("");
        req.setLastName("Smith");
        req.setTrainingTypeId(1L);

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(trainerService);
    }

    @Test
    void create_shouldReturn400_whenTrainingTypeIdIsNull() throws Exception {
        TrainerCreateRequestDto req = new TrainerCreateRequestDto();
        req.setFirstName("John");
        req.setLastName("Smith");
        // trainingTypeId = null

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(trainerService);
    }


    @Test
    void getByUsername_shouldReturn200_withTrainerDetails() throws Exception {
        TrainerResponseDto resp = new TrainerResponseDto();
        resp.setFirstName("John");
        resp.setLastName("Smith");
        resp.setActive(true);
        resp.setSpecialization("Yoga");
        resp.setTrainees(Set.of());

        when(trainerService.getByUsername("John.Smith")).thenReturn(resp);

        mockMvc.perform(get("/api/trainers/John.Smith"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.specialization").value("Yoga"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void getByUsername_shouldReturn404_whenTrainerNotFound() throws Exception {
        when(trainerService.getByUsername("unknown"))
                .thenThrow(new EntityNotFoundException("Trainer 'unknown' not found"));

        mockMvc.perform(get("/api/trainers/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainer 'unknown' not found"));
    }


    @Test
    void update_shouldReturn200_whenRequestIsValid() throws Exception {
        TrainerUpdateRequestDto req = new TrainerUpdateRequestDto();
        req.setUsername("John.Smith");
        req.setFirstName("John");
        req.setLastName("Smith");
        req.setActive(true);

        TrainerUpdateResponseDto resp = new TrainerUpdateResponseDto();
        resp.setUsername("John.Smith");
        resp.setFirstName("John");
        resp.setLastName("Smith");
        resp.setActive(true);
        resp.setTrainees(List.of());

        when(trainerService.update(any())).thenReturn(resp);

        mockMvc.perform(put("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("John.Smith"));
    }

    @Test
    void update_shouldReturn400_whenUsernameIsBlank() throws Exception {
        TrainerUpdateRequestDto req = new TrainerUpdateRequestDto();
        req.setUsername("");
        req.setFirstName("John");
        req.setLastName("Smith");
        req.setActive(true);

        mockMvc.perform(put("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(trainerService);
    }


    @Test
    void getUnassignedActiveTrainers_shouldReturn200_withList() throws Exception {
        TrainerSummaryDto summary = new TrainerSummaryDto("Bob.Jones", "Bob", "Jones", "Cardio");
        when(trainerService.getUnassignedActiveTrainersByTraineeUsername("Jane.Doe"))
                .thenReturn(List.of(summary));

        mockMvc.perform(get("/api/trainers/unassigned/Jane.Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Bob.Jones"))
                .andExpect(jsonPath("$[0].specialization").value("Cardio"));
    }

    @Test
    void getUnassignedActiveTrainers_shouldReturn200_withEmptyList_whenNoneAvailable() throws Exception {
        when(trainerService.getUnassignedActiveTrainersByTraineeUsername("Jane.Doe"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/trainers/unassigned/Jane.Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    void getTrainerTrainings_shouldReturn200_withList() throws Exception {
        TrainerTrainingFilterDto filter = new TrainerTrainingFilterDto();

        TrainingResponseDto training = new TrainingResponseDto();
        when(trainerService.getTrainerTrainingsByFilter(any())).thenReturn(List.of(training));

        mockMvc.perform(post("/api/trainers/trainings/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getTrainerTrainings_shouldReturn200_withEmptyList() throws Exception {
        when(trainerService.getTrainerTrainingsByFilter(any())).thenReturn(List.of());

        mockMvc.perform(post("/api/trainers/trainings/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    void toggleStatus_shouldReturn200() throws Exception {
        doNothing().when(trainerService).toggleStatus("John.Smith");

        mockMvc.perform(patch("/api/trainers/John.Smith/status"))
                .andExpect(status().isOk());

        verify(trainerService).toggleStatus("John.Smith");
    }

    @Test
    void toggleStatus_shouldReturn404_whenTrainerNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Trainer 'unknown' not found"))
                .when(trainerService).toggleStatus("unknown");

        mockMvc.perform(patch("/api/trainers/unknown/status"))
                .andExpect(status().isNotFound());
    }
}
