package com.gym_report.controller;

import com.gym_report.model.TrainerWorkloadSummary;
import com.gym_report.service.TrainerWorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadControllerTest {

    @Mock
    private TrainerWorkloadService workloadService;

    @InjectMocks
    private TrainerWorkloadController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    private String validBody() {
        return """
                {
                  "trainerUsername":  "john.doe",
                  "trainerFirstName": "John",
                  "trainerLastName":  "Doe",
                  "isActive":         true,
                  "trainingDate":     "2025-04-15",
                  "trainingDuration": 90,
                  "actionType":       "ADD"
                }
                """;
    }



    @Test
    void updateWorkload_shouldReturn200_whenRequestIsValid() throws Exception {
        doNothing().when(workloadService).processWorkload(any(), any());

        mockMvc.perform(post("/api/trainer-workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody()))
                .andExpect(status().isOk());

        verify(workloadService).processWorkload(any(), any());
    }

    @Test
    void updateWorkload_shouldPassTransactionIdToService() throws Exception {
        doNothing().when(workloadService).processWorkload(any(), any());

        mockMvc.perform(post("/api/trainer-workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Transaction-Id", "tx-abc-123")
                        .content(validBody()))
                .andExpect(status().isOk());

        verify(workloadService).processWorkload(any(), eq("tx-abc-123"));
    }

    @Test
    void updateWorkload_shouldReturn400_whenUsernameIsBlank() throws Exception {
        String body = """
                {
                  "trainerUsername":  "",
                  "trainerFirstName": "John",
                  "trainerLastName":  "Doe",
                  "isActive":         true,
                  "trainingDate":     "2025-04-15",
                  "trainingDuration": 90,
                  "actionType":       "ADD"
                }
                """;

        mockMvc.perform(post("/api/trainer-workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(workloadService);
    }

    @Test
    void updateWorkload_shouldReturn400_whenActionTypeIsMissing() throws Exception {
        String body = """
                {
                  "trainerUsername":  "john.doe",
                  "trainerFirstName": "John",
                  "trainerLastName":  "Doe",
                  "isActive":         true,
                  "trainingDate":     "2025-04-15",
                  "trainingDuration": 90
                }
                """;

        mockMvc.perform(post("/api/trainer-workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(workloadService);
    }

    @Test
    void updateWorkload_shouldReturn400_whenTrainingDateIsMissing() throws Exception {
        String body = """
                {
                  "trainerUsername":  "john.doe",
                  "trainerFirstName": "John",
                  "trainerLastName":  "Doe",
                  "isActive":         true,
                  "trainingDuration": 90,
                  "actionType":       "ADD"
                }
                """;

        mockMvc.perform(post("/api/trainer-workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateWorkload_shouldReturn400_whenBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/api/trainer-workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateWorkload_shouldReturn200_withDeleteActionType() throws Exception {
        String body = """
                {
                  "trainerUsername":  "john.doe",
                  "trainerFirstName": "John",
                  "trainerLastName":  "Doe",
                  "isActive":         true,
                  "trainingDate":     "2025-04-15",
                  "trainingDuration": 60,
                  "actionType":       "DELETE"
                }
                """;

        doNothing().when(workloadService).processWorkload(any(), any());

        mockMvc.perform(post("/api/trainer-workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }


    @Test
    void getWorkload_shouldReturn200WithBody_whenTrainerExists() throws Exception {
        Map<Integer, Integer> months = new HashMap<>();
        months.put(4, 180);
        Map<Integer, Map<Integer, Integer>> years = new HashMap<>();
        years.put(2025, months);

        TrainerWorkloadSummary summary =
                new TrainerWorkloadSummary("john.doe", "John", "Doe", true, years);

        when(workloadService.getWorkload("john.doe")).thenReturn(summary);

        mockMvc.perform(get("/api/trainer-workload/john.doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.years['2025']['4']").value(180));
    }

    @Test
    void getWorkload_shouldReturn404_whenTrainerNotFound() throws Exception {
        when(workloadService.getWorkload("unknown")).thenReturn(null);

        mockMvc.perform(get("/api/trainer-workload/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getWorkload_shouldReturn404_withTransactionIdHeader() throws Exception {
        when(workloadService.getWorkload("unknown")).thenReturn(null);

        mockMvc.perform(get("/api/trainer-workload/unknown")
                        .header("X-Transaction-Id", "trace-xyz"))
                .andExpect(status().isNotFound());
    }
}
