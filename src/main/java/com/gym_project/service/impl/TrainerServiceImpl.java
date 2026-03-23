package com.gym_project.service.impl;

import com.gym_project.dto.create.request.TrainerCreateRequestDto;
import com.gym_project.dto.create.response.TrainerCreateResponseDto;
import com.gym_project.dto.filter.TrainerTrainingFilterDto;
import com.gym_project.dto.request.TrainerTrainingsRequestDto;
import com.gym_project.dto.response.TrainerResponseDto;
import com.gym_project.dto.response.TrainerSummaryDto;
import com.gym_project.dto.response.TrainingResponseDto;
import com.gym_project.dto.update.request.TrainerUpdateRequestDto;
import com.gym_project.dto.update.response.TrainerUpdateResponseDto;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.TrainingType;
import com.gym_project.exception.EntityNotFoundException;
import com.gym_project.exception.ForbiddenOperationException;
import com.gym_project.mapper.TrainerMapper;
import com.gym_project.mapper.TrainingMapper;
import com.gym_project.repository.TrainerRepository;
import com.gym_project.repository.TrainingRepository;
import com.gym_project.repository.TrainingTypeRepository;
import com.gym_project.security.AuthContext;
import com.gym_project.service.TrainerService;
import com.gym_project.utils.PasswordGenerator;
import com.gym_project.utils.UsernameGenerator;
import lombok.extern.slf4j.Slf4j;
import com.gym_project.security.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;

    public TrainerServiceImpl(
            TrainerRepository trainerRepository,
            TrainerMapper trainerMapper,
            TrainingMapper trainingMapper,
            TrainingTypeRepository trainingTypeRepository,
            TrainingRepository trainingRepository
    ) {
        this.trainerRepository = trainerRepository;
        this.trainerMapper = trainerMapper;
        this.trainingMapper = trainingMapper;
        this.trainingTypeRepository = trainingTypeRepository;
        this.trainingRepository = trainingRepository;
    }

    @Override
    @Transactional
    public TrainerCreateResponseDto create(TrainerCreateRequestDto dto) {
        log.debug("Creating trainer for firstName={}, lastName={}", dto.getFirstName(), dto.getLastName());

        String base = dto.getFirstName() + "." + dto.getLastName();
        List<String> existingUsernames = trainerRepository.findUsernamesStartingWith(base);

        TrainingType trainingType = trainingTypeRepository.findById(dto.getTrainingTypeId())
                .orElseThrow(() -> {
                    log.warn("Training type not found: id={}", dto.getTrainingTypeId());
                    return new EntityNotFoundException("Training type not found with id: " + dto.getTrainingTypeId());
                });

        Trainer trainer = new Trainer();
        trainer.setFirstName(dto.getFirstName());
        trainer.setLastName(dto.getLastName());
        trainer.setActive(true);
        trainer.setSpecialization(trainingType);
        trainer.setUsername(UsernameGenerator.generate(
                trainer.getFirstName(), trainer.getLastName(), existingUsernames));
        trainer.setPassword(PasswordGenerator.generate());

        trainerRepository.save(trainer);

        log.info("Trainer created with username='{}'", trainer.getUsername());
        return trainerMapper.toCreateResponseDto(trainer);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("#username == authentication.name")
    public TrainerResponseDto getByUsername(String username) {
        log.debug("Fetching trainer by username='{}'", username);

        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainer not found: username='{}'", username);
                    return new EntityNotFoundException("Trainer not found: " + username);
                });

        log.debug("Trainer found: username='{}'", username);
        return trainerMapper.toResponseDto(trainer);
    }

    @Override
    @Transactional
    @PreAuthorize("#dto.username == authentication.name")
    public TrainerUpdateResponseDto update(TrainerUpdateRequestDto dto) {
        log.debug("Updating trainer username='{}'", dto.getUsername());

        Trainer trainer = trainerRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> {
                    log.warn("Trainer not found for update: username='{}'", dto.getUsername());
                    return new EntityNotFoundException("Trainer not found: " + dto.getUsername());
                });

        trainer.setFirstName(dto.getFirstName());
        trainer.setLastName(dto.getLastName());
        trainerRepository.update(trainer);

        log.info("Trainer updated: username='{}'", dto.getUsername());
        return trainerMapper.toUpdateResponseDto(trainer);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('TRAINER') or (hasRole('TRAINEE') and #username == authentication.name)")
    public List<TrainerSummaryDto> getUnassignedActiveTrainersByTraineeUsername(String username) {
        log.debug("Fetching unassigned active trainers for trainee username='{}'", username);

        List<Trainer> trainers = trainerRepository.findUnassignedActiveTrainersByTraineeUsername(username);

        log.debug("Found {} unassigned active trainers for username='{}'", trainers.size(), username);

        return trainerMapper.toUpdateResponseDtoList(trainers)
                .stream()
                .map(t -> {
                    TrainerSummaryDto dto = new TrainerSummaryDto();
                    dto.setUsername(t.getUsername());
                    dto.setFirstName(t.getFirstName());
                    dto.setLastName(t.getLastName());
                    dto.setSpecialization(t.getSpecialization());
                    return dto;
                }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("#dto.username == authentication.name")
    public List<TrainingResponseDto> getTrainerTrainingsByFilter(TrainerTrainingFilterDto dto) {
        log.debug("Fetching trainings for trainer username='{}', filter={}", dto.getUsername(), dto);

        TrainerTrainingsRequestDto repoFilter = new TrainerTrainingsRequestDto();
        repoFilter.setUsername(dto.getUsername());
        repoFilter.setPeriodFrom(dto.getFromDate());
        repoFilter.setPeriodTo(dto.getToDate());
        repoFilter.setTraineeName(dto.getTraineeName());

        List<TrainingResponseDto> result = trainingMapper.toResponseDtoList(
                trainingRepository.findByTrainerFilter(repoFilter));

        log.debug("Found {} trainings for trainer username='{}'", result.size(), dto.getUsername());
        return result;
    }

    @Override
    @Transactional
    @PreAuthorize("#username == authentication.name")
    public void toggleStatus(String username) {
        String authenticatedUser = AuthContext.getUsername();
        if(!authenticatedUser.equals(username) || !AuthContext.getRole().toString().equals("TRAINER")){
            throw new ForbiddenOperationException("The user is authenticated but not authorized to perform that action on another user's resource.");
        }
        log.debug("Toggling status for trainer username='{}'", username);
        trainerRepository.toggleStatus(username);
        log.info("Trainer status toggled: username='{}'", username);
    }
}