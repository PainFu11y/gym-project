package com.gym_project.dto.update;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TrainingUpdateDto {

    private String traineeUsername;
    private String trainerUsername;
    private String trainingTypeName;

    private String trainingName;
    private LocalDate trainingDate;
    private Integer trainingDuration;
}
