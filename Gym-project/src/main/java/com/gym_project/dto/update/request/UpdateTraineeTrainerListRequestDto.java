package com.gym_project.dto.update.request;

import com.gym_project.dto.common.TrainerUsernameDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateTraineeTrainerListRequestDto {

    @NotBlank(message = "Trainee username is required")
    private String traineeUsername;

    @NotEmpty(message = "Trainers list cannot be empty")
    @Valid
    private List<TrainerUsernameDto> trainers;
}