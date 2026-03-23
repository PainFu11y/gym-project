package com.gym_project.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
public class TrainerResponseDto {

    private String username;
    private String firstName;
    private String lastName;
    private boolean active;

    private String specialization;

    private Set<String> traineeUsernames;
}