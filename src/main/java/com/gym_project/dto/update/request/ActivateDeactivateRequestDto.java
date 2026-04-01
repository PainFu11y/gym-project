package com.gym_project.dto.update.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ActivateDeactivateRequestDto {

    @NotBlank(message = "Username is required")
    private String username;

    @NotNull(message = "Active status is required")
    private Boolean active;
}