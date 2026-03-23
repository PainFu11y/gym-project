package com.gym_project.dto.create;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TraineeCreateDto {
    private String firstName;
    private String lastName;

    private LocalDate dateOfBirth;
    private String address;
}