package com.gym_project.dto.update.response;

import com.gym_project.dto.response.TraineeSummaryResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TrainerUpdateResponseDto {

    private String username;
    private String firstName;
    private String lastName;

    private String specialization;

    private Boolean active;

    private List<TraineeSummaryResponseDto> trainees;
}