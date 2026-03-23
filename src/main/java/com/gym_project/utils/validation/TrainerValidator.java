package com.gym_project.utils.validation;

import com.gym_project.dto.create.TrainerCreateDto;
import com.gym_project.dto.update.TrainerUpdateDto;
import org.springframework.stereotype.Component;

@Component
public class TrainerValidator {

    public static void validateCreate(TrainerCreateDto dto) {
        requireNotBlank(dto.getFirstName(), "First name");
        requireNotBlank(dto.getLastName(), "Last name");
        requireNotBlank(dto.getSpecialization(), "Specialization");
    }

    public static void validateUpdate(TrainerUpdateDto dto) {

        if (dto.getFirstName() != null)
            requireNotBlank(dto.getFirstName(), "First name");

        if (dto.getLastName() != null)
            requireNotBlank(dto.getLastName(), "Last name");

        if (dto.getSpecialization() != null)
            requireNotBlank(dto.getSpecialization(), "Specialization");
    }

    private static void requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
    }
}