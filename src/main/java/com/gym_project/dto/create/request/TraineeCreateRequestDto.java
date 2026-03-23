package com.gym_project.dto.create.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
public class TraineeCreateRequestDto {

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
}