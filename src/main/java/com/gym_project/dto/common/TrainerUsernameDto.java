package com.gym_project.dto.common;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class TrainerUsernameDto {

    @NotBlank(message = "Trainer username is required")
    private String username;
}