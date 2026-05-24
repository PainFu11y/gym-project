package com.gym_project.cucumber.integration;

import com.gym_project.actuator.metrics.GymMetrics;
import com.gym_project.dto.create.request.TrainingCreateRequestDto;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.TrainingType;
import com.gym_project.exception.EntityNotFoundException;
import com.gym_project.messaging.WorkloadMessage;
import com.gym_project.messaging.WorkloadMessageProducer;
import com.gym_project.repository.TraineeRepository;
import com.gym_project.repository.TrainerRepository;
import com.gym_project.repository.TrainingRepository;
import com.gym_project.service.impl.TrainingServiceImpl;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TrainingWorkloadIntegrationSteps {

    private TrainingRepository  trainingRepository;
    private TrainerRepository   trainerRepository;
    private TraineeRepository   traineeRepository;
    private WorkloadMessageProducer workloadProducer;
    private GymMetrics          gymMetrics;
    private TrainingServiceImpl trainingService;

    private ArgumentCaptor<WorkloadMessage> messageCaptor;
    private Throwable thrownException;

    @Before
    public void setUp() {
        trainingRepository = mock(TrainingRepository.class);
        trainerRepository  = mock(TrainerRepository.class);
        traineeRepository  = mock(TraineeRepository.class);
        workloadProducer   = mock(WorkloadMessageProducer.class);
        gymMetrics         = mock(GymMetrics.class);
        messageCaptor      = ArgumentCaptor.forClass(WorkloadMessage.class);

        trainingService = new TrainingServiceImpl(
                trainingRepository, trainerRepository, traineeRepository,
                workloadProducer, gymMetrics);

        SecurityContextHolder.clearContext();
    }


    @Given("a trainer {string} with firstName {string} and lastName {string} is registered")
    public void aTrainerIsRegistered(String username, String firstName, String lastName) {
        Trainer trainer = buildTrainer(username, firstName, lastName);
        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));

        setSecurityContext(username, "ROLE_TRAINER");
    }

    @Given("a trainee {string} is registered")
    public void aTraineeIsRegistered(String username) {
        Trainee trainee = buildTrainee(username);
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
    }

    @Given("a training with id {long} belonging to trainer {string} with duration {int} minutes exists")
    public void aTrainingExists(Long id, String trainerUsername, int duration) {
        Trainer trainer = buildTrainer(trainerUsername, "John", "Trainer");
        com.gym_project.entity.Training training = new com.gym_project.entity.Training();
        training.setTrainer(trainer);
        training.setTrainingDate(LocalDate.of(2024, 3, 10));
        training.setTrainingDuration(duration);

        when(trainingRepository.findById(id)).thenReturn(Optional.of(training));
        doNothing().when(trainingRepository).delete(id);

        setSecurityContext(trainerUsername, "ROLE_TRAINER");
    }

    @Given("a trainer {string} exists")
    public void aTrainerExists(String username) {
        Trainer trainer = buildTrainer(username, "John", "Trainer");
        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));
        setSecurityContext(username, "ROLE_TRAINER");
    }

    @Given("no trainee with username {string} exists")
    public void noTraineeExists(String username) {
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());
    }

    @Given("a trainee {string} exists")
    public void aTraineeExists(String username) {
        Trainee trainee = buildTrainee(username);
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
    }

    @Given("no trainer with username {string} exists")
    public void noTrainerExists(String username) {
        when(trainerRepository.findByUsername(username)).thenReturn(Optional.empty());
    }


    @When("a training session is created for trainer {string} and trainee {string} lasting {int} minutes")
    public void createTrainingSession(String trainerUsername, String traineeUsername, int duration) {
        TrainingCreateRequestDto dto = new TrainingCreateRequestDto();
        dto.setTraineeUsername(traineeUsername);
        dto.setTrainerUsername(trainerUsername);
        dto.setTrainingName("Test Session");
        dto.setTrainingDate(LocalDate.of(2024, 6, 15));
        dto.setTrainingDuration(duration);

        trainingService.create(dto);
    }

    @When("the training with id {long} is deleted by trainer {string}")
    public void deleteTraining(Long id, String trainerUsername) {
        trainingService.delete(id);
    }

    @When("a training is attempted for trainer {string} and trainee {string}")
    public void attemptTraining(String trainerUsername, String traineeUsername) {
        TrainingCreateRequestDto dto = new TrainingCreateRequestDto();
        dto.setTraineeUsername(traineeUsername);
        dto.setTrainerUsername(trainerUsername);
        dto.setTrainingName("Test");
        dto.setTrainingDate(LocalDate.of(2024, 6, 15));
        dto.setTrainingDuration(60);

        thrownException = null;
        try {
            trainingService.create(dto);
        } catch (EntityNotFoundException ex) {
            thrownException = ex;
        }
    }


    @Then("a workload message with action ADD is sent for trainer {string}")
    public void workloadAddMessageSent(String trainerUsername) {
        verify(workloadProducer).send(messageCaptor.capture());
        WorkloadMessage msg = messageCaptor.getValue();
        assertThat(msg.getTrainerUsername()).isEqualTo(trainerUsername);
        assertThat(msg.getActionType()).isEqualTo(WorkloadMessage.ActionType.ADD);
    }

    @Then("a workload message with action DELETE is sent for trainer {string}")
    public void workloadDeleteMessageSent(String trainerUsername) {
        verify(workloadProducer).send(messageCaptor.capture());
        WorkloadMessage msg = messageCaptor.getValue();
        assertThat(msg.getTrainerUsername()).isEqualTo(trainerUsername);
        assertThat(msg.getActionType()).isEqualTo(WorkloadMessage.ActionType.DELETE);
    }

    @And("the message contains training duration {int} minutes")
    public void messageContainsDuration(int expectedDuration) {
        WorkloadMessage msg = messageCaptor.getValue();
        assertThat(msg.getTrainingDuration()).isEqualTo(expectedDuration);
    }

    @Then("no workload message is sent")
    public void noWorkloadMessageSent() {
        assertThat(thrownException).isNotNull();
        verifyNoInteractions(workloadProducer);
    }


    private Trainer buildTrainer(String username, String firstName, String lastName) {
        Trainer trainer = new Trainer();
        trainer.setUsername(username);
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setActive(true);
        trainer.setSpecialization(new TrainingType());
        return trainer;
    }

    private Trainee buildTrainee(String username) {
        Trainee trainee = new Trainee();
        trainee.setUsername(username);
        trainee.setFirstName("Jane");
        trainee.setLastName("Trainee");
        trainee.setActive(true);
        return trainee;
    }

    private void setSecurityContext(String username, String role) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                username, null, List.of(new SimpleGrantedAuthority(role)));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
