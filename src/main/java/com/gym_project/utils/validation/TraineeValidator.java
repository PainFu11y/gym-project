package com.gym_project.utils.validation;

import com.gym_project.dto.create.TraineeCreateDto;
import com.gym_project.dto.update.TraineeUpdateDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TraineeValidator {

    public static void validateCreate(TraineeCreateDto dto) {

        requireNotBlank(dto.getFirstName(), "First name");
        requireNotBlank(dto.getLastName(), "Last name");

        if (dto.getDateOfBirth() == null) {
            throw new IllegalArgumentException("Date of birth cannot be null");
        }

        if (dto.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth must be in the past");
        }

        requireNotBlank(dto.getAddress(), "Address");
    }

    public static void validateUpdate(TraineeUpdateDto dto) {

        if (dto.getFirstName() != null) {
            requireNotBlank(dto.getFirstName(), "First name");
        }

        if (dto.getLastName() != null) {
            requireNotBlank(dto.getLastName(), "Last name");
        }

        if (dto.getAddress() != null) {
            requireNotBlank(dto.getAddress(), "Address");
        }

        if (dto.getDateOfBirth() != null &&
                dto.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth must be in the past");
        }
    }

    private static void requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
    }
}