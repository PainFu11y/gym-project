package com.gym_project.service.impl;

import com.gym_project.dto.response.TrainingTypeResponseDto;
import com.gym_project.entity.TrainingType;
import com.gym_project.repository.TrainingTypeRepository;
import com.gym_project.service.TrainingTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingTypeServiceImpl(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingTypeResponseDto> findAll() {
        log.debug("Fetching all training types");

        List<TrainingType> trainingTypes = trainingTypeRepository.findAll();

        log.debug("Found {} training types", trainingTypes.size());

        return trainingTypes.stream()
                .map(this::mapToDto)
                .toList();
    }

    private TrainingTypeResponseDto mapToDto(TrainingType entity) {
        TrainingTypeResponseDto dto = new TrainingTypeResponseDto();
        dto.setId(entity.getId());
        dto.setTrainingType(entity.getTrainingTypeName());
        return dto;
    }
}