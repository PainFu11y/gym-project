package com.gym_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym_project.dto.create.request.TrainingCreateRequestDto;
import com.gym_project.exception.EntityNotFoundException;
import com.gym_project.exception.ForbiddenOperationException;
import com.gym_project.exception.GlobalExceptionHandler;
import com.gym_project.service.TrainingService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private TrainingController controller;

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

    private TrainingCreateRequestDto validRequest() {
        TrainingCreateRequestDto dto = new TrainingCreateRequestDto();
        dto.setTraineeUsername("Jane.Doe");
        dto.setTrainerUsername("John.Smith");
        dto.setTrainingName("Morning Yoga");
        dto.setTrainingDate(LocalDate.of(2025, 4, 15));
        dto.setTrainingDuration(60);
        return dto;
    }

    // ------------------------------------------------------------------ create

    @Test
    void create_shouldReturn200_whenRequestIsValid() throws Exception {
        doNothing().when(trainingService).create(any());

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isOk());

        verify(trainingService).create(any());
    }

    @Test
    void create_shouldReturn400_whenTraineeUsernameIsBlank() throws Exception {
        TrainingCreateRequestDto req = validRequest();
        req.setTraineeUsername("");

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(trainingService);
    }

    @Test
    void create_shouldReturn400_whenTrainerUsernameIsBlank() throws Exception {
        TrainingCreateRequestDto req = validRequest();
        req.setTrainerUsername("");

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(trainingService);
    }

    @Test
    void create_shouldReturn400_whenTrainingNameIsBlank() throws Exception {
        TrainingCreateRequestDto req = validRequest();
        req.setTrainingName("");

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(trainingService);
    }

    @Test
    void create_shouldReturn400_whenTrainingDateIsNull() throws Exception {
        TrainingCreateRequestDto req = validRequest();
        req.setTrainingDate(null);

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(trainingService);
    }

    @Test
    void create_shouldReturn400_whenDurationIsZero() throws Exception {
        TrainingCreateRequestDto req = validRequest();
        req.setTrainingDuration(0);

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(trainingService);
    }

    @Test
    void create_shouldReturn404_whenTraineeNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Trainee 'Jane.Doe' not found"))
                .when(trainingService).create(any());

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainee 'Jane.Doe' not found"));
    }

    @Test
    void create_shouldReturn404_whenTrainerNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Trainer 'John.Smith' not found"))
                .when(trainingService).create(any());

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainer 'John.Smith' not found"));
    }

    // ------------------------------------------------------------------ delete

    @Test
    void delete_shouldReturn200_whenTrainingExists() throws Exception {
        doNothing().when(trainingService).delete(10L);

        mockMvc.perform(delete("/api/trainings/10"))
                .andExpect(status().isOk());

        verify(trainingService).delete(10L);
    }

    @Test
    void delete_shouldReturn404_whenTrainingNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Training '999' not found"))
                .when(trainingService).delete(999L);

        mockMvc.perform(delete("/api/trainings/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Training '999' not found"));
    }

    @Test
    void delete_shouldReturn400_whenCallerIsNotOwner() throws Exception {
        doThrow(new ForbiddenOperationException("Only the assigned trainer can delete this training"))
                .when(trainingService).delete(10L);

        mockMvc.perform(delete("/api/trainings/10"))
                .andExpect(status().isBadRequest());
    }
}
