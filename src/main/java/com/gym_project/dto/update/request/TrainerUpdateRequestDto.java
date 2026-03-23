package com.gym_project.dto.update.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class TrainerUpdateRequestDto {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "First Name is required")
    @Size(max = 50, message = "First Name cannot be longer than 50 characters")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Size(max = 50, message = "Last Name cannot be longer than 50 characters")
    private String lastName;

    @NotNull(message = "Active status is required")
    private Boolean active;
}