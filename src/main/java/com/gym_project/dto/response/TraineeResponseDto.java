package com.gym_project.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Set;


@Getter
@Setter
@ToString
public class TraineeResponseDto {

    private String username;
    private String firstName;
    private String lastName;
    private boolean active;

    private LocalDate dateOfBirth;
    private String address;

    private Set<String> trainerUsernames;
}