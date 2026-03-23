package com.gym_project.dto.create.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class TrainerCreateRequestDto {

    @NotBlank(message = "First Name is required")
    @Size(max = 50, message = "First Name cannot be longer than 50 characters")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Size(max = 50, message = "Last Name cannot be longer than 50 characters")
    private String lastName;

    @NotNull(message = "Specialization (TrainingType ID) is required")
    private Long trainingTypeId;
}