package com.gym_project.dto.update;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TraineeUpdateDto {

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String address;
    private boolean active;
}