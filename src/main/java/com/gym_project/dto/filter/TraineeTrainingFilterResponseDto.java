package com.gym_project.dto.filter;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Getter
@Setter
public class TraineeTrainingFilterResponseDto {

    @NotBlank(message = "Username is required")
    private String username;

    @PastOrPresent(message = "From date cannot be in the future")
    private LocalDate fromDate;

    @PastOrPresent(message = "To date cannot be in the future")
    private LocalDate toDate;

    private String trainerName;

    private String trainingTypeName;
}