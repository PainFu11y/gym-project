package com.gym_project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainerSummaryDto {
    private String username;
    private String firstName;
    private String lastName;
    private String specialization;
}