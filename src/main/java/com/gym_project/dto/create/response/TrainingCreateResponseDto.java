package com.gym_project.dto.create.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class TrainingCreateResponseDto {
    private Long id;

    private String traineeUsername;
    private String trainerUsername;
    private String trainingTypeName;

    private String trainingName;
    private LocalDate trainingDate;
    private Integer trainingDuration;
}