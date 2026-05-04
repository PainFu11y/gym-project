package com.gym_report.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TrainerWorkloadRequestDto {

    @NotBlank
    private String trainerUsername;

    @NotBlank
    private String trainerFirstName;

    @NotBlank
    private String trainerLastName;

    @NotNull
    private Boolean isActive;

    @NotNull
    private LocalDate trainingDate;

    @NotNull
    private Integer trainingDuration;

    @NotNull
    private ActionType actionType;

    public enum ActionType {
        ADD, DELETE
    }
}
