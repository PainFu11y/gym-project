package com.gym_project.service.impl;

import com.gym_project.dto.create.request.TraineeCreateRequestDto;
import com.gym_project.dto.create.response.TraineeCreateResponseDto;
import com.gym_project.dto.request.LoginRequestDto;
import com.gym_project.dto.request.TraineeTrainingsFilterRequestDto;
import com.gym_project.dto.response.TraineeResponseDto;
import com.gym_project.dto.response.TrainerResponseDto;
import com.gym_project.dto.response.TrainerSummaryDto;
import com.gym_project.dto.response.TrainingResponseDto;
import com.gym_project.dto.update.request.TraineeUpdateRequestDto;
import com.gym_project.dto.update.request.UpdateTraineeTrainerListRequestDto;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.Training;
import com.gym_project.exception.AccessDeniedException;
import com.gym_project.exception.EntityNotFoundException;
import com.gym_project.exception.ForbiddenOperationException;
import com.gym_project.exception.InvalidCredentialsException;
import com.gym_project.mapper.TraineeMapper;
import com.gym_project.mapper.TrainerMapper;
import com.gym_project.mapper.TrainingMapper;
import com.gym_project.repository.TraineeRepository;
import com.gym_project.repository.TrainerRepository;
import com.gym_project.repository.TrainingRepository;
import com.gym_project.security.AuthContext;
import com.gym_project.security.PreAuthorize;
import com.gym_project.service.TraineeService;
import com.gym_project.utils.PasswordGenerator;
import com.gym_project.utils.UsernameGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;

    public TraineeServiceImpl(
            TraineeRepository traineeRepository,
            TrainerRepository trainerRepository,
            TrainingRepository trainingRepository,
            TraineeMapper traineeMapper,
            TrainerMapper trainerMapper,
            TrainingMapper trainingMapper
    ) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingRepository = trainingRepository;
        this.traineeMapper = traineeMapper;
        this.trainerMapper = trainerMapper;
        this.trainingMapper = trainingMapper;
    }

    @Override
    @Transactional
    public TraineeCreateResponseDto create(TraineeCreateRequestDto dto) {
        log.debug("Creating trainee for firstName={}, lastName={}", dto.getFirstName(), dto.getLastName());

        String base = dto.getFirstName() + "." + dto.getLastName();
        List<String> existingUsernames = traineeRepository.findUsernamesStartingWith(base);
        String generatedUsername =
                UsernameGenerator.generate(dto.getFirstName(), dto.getLastName(), existingUsernames);

        Trainee trainee = traineeMapper.toEntity(dto);
        trainee.setUsername(generatedUsername);
        trainee.setPassword(PasswordGenerator.generate());
        traineeRepository.save(trainee);

        log.info("Trainee created with username='{}'", generatedUsername);
        return traineeMapper.toCreateResponseDto(trainee);
    }

    @Override
    @Transactional
    @PreAuthorize("#username == authentication.name")
    public TraineeResponseDto getByUsername(String username) {
        log.debug("Fetching trainee by username='{}'", username);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainee not found: username='{}'", username);
                    return new EntityNotFoundException("Trainee not found: " + username);
                });

        log.debug("Trainee found: username='{}'", username);
        return traineeMapper.toResponseDto(trainee);
    }

    @Override
    @Transactional
    @PreAuthorize("#dto.username == authentication.name")
    public TraineeResponseDto update(TraineeUpdateRequestDto dto) {
        log.debug("Updating trainee username='{}'", dto.getUsername());
        String authenticatedUser = AuthContext.getUsername();
        if(!authenticatedUser.equals(dto.getUsername())){
            throw new AccessDeniedException("The user is authenticated but not authorized to perform that action on another user's resource.");
        }
        Trainee trainee = traineeRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> {
                    log.warn("Trainee not found for update: username='{}'", dto.getUsername());
                    return new EntityNotFoundException("Trainee not found: " + dto.getUsername());
                });

        traineeMapper.updateEntity(dto, trainee);
        TraineeResponseDto result = traineeMapper.toResponseDto(traineeRepository.update(trainee));

        log.info("Trainee updated: username='{}'", dto.getUsername());
        return result;
    }

    @Override
    @Transactional
    @PreAuthorize("#username == authentication.name")
    public void deleteByUsername(String username) {
        log.debug("Deleting trainee username='{}'", username);
        String authenticatedUser = AuthContext.getUsername();
        if(!authenticatedUser.equals(username)){
            throw new AccessDeniedException("The user is authenticated but not authorized to perform that action on another user's resource.");
        }
        traineeRepository.deleteByUsername(username);
        log.info("Trainee deleted: username='{}'", username);
    }

    @Override
    @Transactional
    @PreAuthorize("#username == authentication.name")
    public void toggleStatus(String username) {
        String authenticatedUser = AuthContext.getUsername();
        if(!authenticatedUser.equals(username) || !AuthContext.getRole().toString().equals("TRAINEE")){
            throw new ForbiddenOperationException("The user is authenticated but not authorized to perform that action on another user's resource.");
        }
        log.debug("Toggling status for trainee username='{}'", username);
        traineeRepository.toggleStatus(username);
        log.info("Trainee status toggled: username='{}'", username);
    }

    @Override
    @Transactional
    public List<TrainingResponseDto> getTraineeTrainings(TraineeTrainingsFilterRequestDto filter) {
        log.debug("Fetching trainings for trainee username='{}', filter={}", filter.getUsername(), filter);
        String authenticatedUser = AuthContext.getUsername();
        if(!authenticatedUser.equals(filter.getUsername())){
            throw new ForbiddenOperationException("The user is authenticated but not authorized to perform that action on another user's resource.");
        }
        List<Training> trainings = trainingRepository.findByTraineeFilter(filter);
        log.debug("Found {} trainings for username='{}'", trainings.size(), filter.getUsername());
        return trainingMapper.toResponseDtoList(trainings);
    }

    @Override
    public TraineeResponseDto validateCredentials(LoginRequestDto dto) {
        log.debug("Validating credentials for username='{}'", dto.getUsername());

        Trainee trainee = traineeRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> {
                    log.warn("Credential validation failed - username not found: '{}'", dto.getUsername());
                    return new InvalidCredentialsException();
                });

        if (!trainee.getPassword().equals(dto.getPassword())) {
            log.warn("Credential validation failed - wrong password for username='{}'", dto.getUsername());
            throw new InvalidCredentialsException();
        }

        log.debug("Credentials valid for username='{}'", dto.getUsername());
        return traineeMapper.toResponseDto(trainee);
    }

    @Override
    @PreAuthorize("#username == authentication.name")
    public List<TrainerResponseDto> getTrainers(String username) {
        log.debug("Fetching trainers for trainee username='{}'", username);

        List<Trainer> trainers = trainerRepository
                .findTraineesByTrainerUsername(username)
                .stream()
                .flatMap(t -> t.getTrainers().stream())
                .collect(Collectors.toList());

        log.debug("Found {} trainers for username='{}'", trainers.size(), username);
        return trainerMapper.toResponseDtoList(trainers);
    }

    @Override
    @Transactional
    @PreAuthorize("#dto.traineeUsername == authentication.name")
    public List<TrainerSummaryDto> updateTrainerList(UpdateTraineeTrainerListRequestDto dto) {
        log.debug("Updating trainer list for trainee username='{}'", dto.getTraineeUsername());

        Trainee trainee = traineeRepository.findByUsername(dto.getTraineeUsername())
                .orElseThrow(() -> {
                    log.warn("Trainee not found for trainer list update: username='{}'", dto.getTraineeUsername());
                    return new EntityNotFoundException("Trainee not found: " + dto.getTraineeUsername());
                });

        List<String> trainerUsernames = dto.getTrainers()
                .stream()
                .map(t -> t.getUsername())
                .toList();

        log.debug("New trainer list for '{}': {}", dto.getTraineeUsername(), trainerUsernames);

        List<Trainer> trainers = trainerRepository.findTrainersByUsernames(trainerUsernames);

        trainerRepository.findAll()
                .forEach(t -> t.getTrainees().remove(trainee));
        trainers.forEach(t -> t.getTrainees().add(trainee));

        log.info("Trainer list updated for trainee='{}', assigned {} trainers",
                dto.getTraineeUsername(), trainers.size());

        return trainers.stream()
                .map(tr -> new TrainerSummaryDto(
                        tr.getUsername(),
                        tr.getFirstName(),
                        tr.getLastName(),
                        tr.getSpecialization().getTrainingTypeName()
                ))
                .toList();
    }
}