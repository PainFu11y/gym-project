package com.gym_project.dto.update.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;

@Getter
@Setter
public class TraineeUpdateRequestDto {

    @NotBlank(message = "Username is required")
    @Size(max = 101, message = "First Name cannot be longer than 50 characters")
    private String username;

    @NotBlank(message = "First Name is required")
    @Size(max = 50, message = "First Name cannot be longer than 50 characters")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Size(max = 50, message = "Last Name cannot be longer than 50 characters")
    private String lastName;

    @Past(message = "Date of Birth must be in the past")
    private LocalDate dateOfBirth;

    @Size(max = 250, message = "Address cannot be longer than 250 characters")
    private String address;

    @NotNull(message = "Active status is required")
    private Boolean active;
}