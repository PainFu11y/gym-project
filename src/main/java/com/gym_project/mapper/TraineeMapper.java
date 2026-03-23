package com.gym_project.mapper;

import com.gym_project.dto.create.TraineeCreateDto;
import com.gym_project.dto.update.TraineeUpdateDto;
import com.gym_project.dto.response.TraineeResponseDto;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.User;

import java.util.stream.Collectors;

public class TraineeMapper {


    public static Trainee toEntity(TraineeCreateDto dto) {
        Trainee trainee = new Trainee();

        trainee.setFirstName(dto.getFirstName());
        trainee.setLastName(dto.getLastName());
        trainee.setDateOfBirth(dto.getDateOfBirth());
        trainee.setAddress(dto.getAddress());
        trainee.setActive(true);

        return trainee;
    }

    public static TraineeResponseDto toDto(Trainee trainee) {
        TraineeResponseDto dto = new TraineeResponseDto();

        dto.setUsername(trainee.getUsername());
        dto.setFirstName(trainee.getFirstName());
        dto.setLastName(trainee.getLastName());
        dto.setActive(trainee.isActive());
        dto.setDateOfBirth(trainee.getDateOfBirth());
        dto.setAddress(trainee.getAddress());

        if (trainee.getTrainers() != null) {
            dto.setTrainerUsernames(
                    trainee.getTrainers()
                            .stream()
                            .map(User::getUsername)
                            .collect(Collectors.toSet())
            );
        }

        return dto;
    }

    public static void updateEntity(Trainee trainee, TraineeUpdateDto dto) {

        if (dto.getFirstName() != null) {
            trainee.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            trainee.setLastName(dto.getLastName());
        }
        if (dto.getDateOfBirth() != null) {
            trainee.setDateOfBirth(dto.getDateOfBirth());
        }
        if (dto.getAddress() != null) {
            trainee.setAddress(dto.getAddress());
        }

    }
}