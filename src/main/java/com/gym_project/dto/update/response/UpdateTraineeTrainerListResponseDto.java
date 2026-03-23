package com.gym_project.dto.update.response;


import com.gym_project.dto.response.TrainerSummaryDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateTraineeTrainerListResponseDto {

    private List<TrainerSummaryDto> trainers;
}