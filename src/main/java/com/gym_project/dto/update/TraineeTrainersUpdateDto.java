package com.gym_project.dto.update;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TraineeTrainersUpdateDto {

    private List<String> trainerUsernames;
}