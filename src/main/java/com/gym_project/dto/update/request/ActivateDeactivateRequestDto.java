package com.gym_project.dto.update.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ActivateDeactivateRequestDto {

    @NotBlank(message = "Username is required")
    private String username;

    @NotNull(message = "Active status is required")
    private Boolean active;
}