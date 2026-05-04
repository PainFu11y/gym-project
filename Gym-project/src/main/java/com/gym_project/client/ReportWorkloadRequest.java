package com.gym_project.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ReportWorkloadRequest {

    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;

    @JsonProperty("isActive")
    private boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate trainingDate;

    private int trainingDuration;
    private ActionType actionType;

    public enum ActionType {
        ADD, DELETE
    }
}
