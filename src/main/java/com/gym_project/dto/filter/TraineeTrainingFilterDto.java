package com.gym_project.dto.filter;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TraineeTrainingFilterDto {
    private LocalDate fromDate;
    private LocalDate toDate;
    private String trainerName;
    private String trainingTypeName;
}