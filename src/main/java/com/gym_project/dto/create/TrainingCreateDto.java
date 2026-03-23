package com.gym_project.dto.create;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TrainingCreateDto {

    private String traineeUsername;
    private String trainerUsername;
    private Long trainingTypeId;

    private String trainingName;
    private LocalDate trainingDate;
    private Integer trainingDuration;
}