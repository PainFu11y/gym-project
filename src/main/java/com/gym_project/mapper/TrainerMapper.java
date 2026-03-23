package com.gym_project.mapper;

import com.gym_project.dto.common.TraineeSummaryDto;
import com.gym_project.dto.create.response.TrainerCreateResponseDto;
import com.gym_project.dto.response.TraineeSummaryResponseDto;
import com.gym_project.dto.response.TrainerResponseDto;
import com.gym_project.dto.update.response.TrainerUpdateResponseDto;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.TrainingType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TrainerMapper {

    @Mapping(target = "specialization", source = "specialization", qualifiedByName = "mapTrainingType")
    @Mapping(target = "trainees", source = "trainees", qualifiedByName = "mapTraineeSummary")
    TrainerResponseDto toResponseDto(Trainer trainer);

    List<TrainerResponseDto> toResponseDtoList(List<Trainer> trainers);

    TrainerCreateResponseDto toCreateResponseDto(Trainer trainer);


    @Mapping(target = "specialization", source = "specialization", qualifiedByName = "mapTrainingType")
    @Mapping(target = "trainees", source = "trainees", qualifiedByName = "mapTraineeSummaryResponse")
    TrainerUpdateResponseDto toUpdateResponseDto(Trainer trainer);

    List<TrainerUpdateResponseDto> toUpdateResponseDtoList(List<Trainer> trainers);

    @Named("mapTrainingType")
    default String mapTrainingType(TrainingType type) {
        return type == null ? null : type.getTrainingTypeName();
    }

    @Named("mapTraineeSummary")
    default Set<TraineeSummaryDto> mapTraineeSummary(Set<Trainee> trainees) {
        if (trainees == null) return null;

        return trainees.stream().map(t -> {
            TraineeSummaryDto dto = new TraineeSummaryDto();
            dto.setUsername(t.getUsername());
            dto.setFirstName(t.getFirstName());
            dto.setLastName(t.getLastName());
            return dto;
        }).collect(Collectors.toSet());
    }

    @Named("mapTraineeSummaryResponse")
    default List<TraineeSummaryResponseDto> mapTraineeSummaryResponse(Set<Trainee> trainees) {
        if (trainees == null) return null;

        return trainees.stream().map(t -> {
            TraineeSummaryResponseDto dto = new TraineeSummaryResponseDto();
            dto.setUsername(t.getUsername());
            dto.setFirstName(t.getFirstName());
            dto.setLastName(t.getLastName());
            return dto;
        }).toList();
    }
}