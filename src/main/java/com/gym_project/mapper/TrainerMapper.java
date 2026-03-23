package com.gym_project.mapper;


import com.gym_project.dto.create.TrainerCreateDto;
import com.gym_project.dto.response.TrainerResponseDto;
import com.gym_project.dto.update.TrainerUpdateDto;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.User;

import java.util.stream.Collectors;

public class TrainerMapper {

    public static Trainer toEntity(TrainerCreateDto dto) {

        Trainer trainer = new Trainer();

        trainer.setFirstName(dto.getFirstName());
        trainer.setLastName(dto.getLastName());
        trainer.setSpecialization(dto.getSpecialization());
        trainer.setActive(true);

        return trainer;
    }

    public static TrainerResponseDto toDto(Trainer trainer) {

        TrainerResponseDto dto = new TrainerResponseDto();

        dto.setUsername(trainer.getUsername());
        dto.setFirstName(trainer.getFirstName());
        dto.setLastName(trainer.getLastName());
        dto.setActive(trainer.isActive());
        dto.setSpecialization(trainer.getSpecialization());

        if (trainer.getTrainees() != null) {
            dto.setTraineeUsernames(
                    trainer.getTrainees()
                            .stream()
                            .map(User::getUsername)
                            .collect(Collectors.toSet())
            );
        }


        return dto;
    }

    public static void updateEntity(Trainer trainer, TrainerUpdateDto dto) {

        if (dto.getFirstName() != null) {
            trainer.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            trainer.setLastName(dto.getLastName());
        }

        if (dto.getSpecialization() != null) {
            trainer.setSpecialization(dto.getSpecialization());
        }
    }
}