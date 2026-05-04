package com.gym_project.dto.filter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TrainerTrainingFilterDto {
    @NotBlank(message = "Username is required")
    private String username;

    @PastOrPresent(message = "From date cannot be in the future")
    private LocalDate fromDate;

    @PastOrPresent(message = "To date cannot be in the future")
    private LocalDate toDate;

    private String traineeName;
}