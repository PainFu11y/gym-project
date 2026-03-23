package com.gym_project.mapper;

import com.gym_project.dto.create.response.TrainingCreateResponseDto;
import com.gym_project.dto.response.TrainingResponseDto;
import com.gym_project.entity.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainingMapper {

    TrainingMapper INSTANCE = Mappers.getMapper(TrainingMapper.class);

    @Mapping(target = "traineeUsername", source = "trainee.username")
    @Mapping(target = "trainerUsername", source = "trainer.username")
    @Mapping(target = "trainingTypeName", source = "trainingType.trainingTypeName")
    TrainingCreateResponseDto toCreateResponseDto(Training training);

    default List<TrainingCreateResponseDto> toCreateResponseDtoList(List<Training> trainings) {
        if (trainings == null) return null;
        return trainings.stream()
                .map(this::toCreateResponseDto)
                .toList();
    }

    @Mapping(target = "trainingName", source = "trainingName")
    @Mapping(target = "trainingDate", source = "trainingDate")
    @Mapping(target = "trainingType", source = "trainingType.trainingTypeName")
    @Mapping(target = "trainingDuration", source = "trainingDuration")
    @Mapping(target = "trainerName", expression = "java(training.getTrainer().getFirstName() + \" \" + training.getTrainer().getLastName())")
    TrainingResponseDto toResponseDto(Training training);

    default List<TrainingResponseDto> toResponseDtoList(List<Training> trainings) {
        if (trainings == null) return null;
        return trainings.stream()
                .map(this::toResponseDto)
                .toList();
    }
}