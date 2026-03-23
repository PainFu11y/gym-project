package com.gym_project.dto.update.request;

import com.gym_project.dto.common.TrainerUsernameDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
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