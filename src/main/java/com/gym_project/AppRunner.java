package com.gym_project;

import com.gym_project.dto.create.TraineeCreateDto;
import com.gym_project.dto.create.TrainerCreateDto;
import com.gym_project.dto.create.TrainingCreateDto;
import com.gym_project.dto.filter.TraineeTrainingFilterDto;
import com.gym_project.dto.filter.TrainerTrainingFilterDto;
import com.gym_project.dto.response.TrainerResponseDto;
import com.gym_project.dto.response.TrainingResponseDto;
import com.gym_project.dto.update.TraineeTrainersUpdateDto;
import com.gym_project.dto.update.TraineeUpdateDto;
import com.gym_project.dto.update.TrainerUpdateDto;
import com.gym_project.security.AuthService;
import com.gym_project.security.Role;
import com.gym_project.service.TraineeService;
import com.gym_project.service.TrainerService;
import com.gym_project.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class AppRunner {

    private final AuthService authService;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        while (true) {
            System.out.println("\nGym Management Console");
            System.out.println("=====================");
            System.out.println("1. Register as Trainee");
            System.out.println("2. Register as Trainer");
            System.out.println("3. Login as Trainee");
            System.out.println("4. Login as Trainer");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> registerTrainee();
                case "2" -> registerTrainer();
                case "3" -> login(Role.TRAINEE);
                case "4" -> login(Role.TRAINER);
                case "5" -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void registerTrainee() {
        try {
            TraineeCreateDto dto = new TraineeCreateDto();

            System.out.print("First name: ");
            dto.setFirstName(scanner.nextLine());

            System.out.print("Last name: ");
            dto.setLastName(scanner.nextLine());

            System.out.print("Date of birth (YYYY-MM-DD): ");
            dto.setDateOfBirth(LocalDate.parse(scanner.nextLine()));

            System.out.print("Address: ");
            dto.setAddress(scanner.nextLine());

            var trainee = traineeService.create(dto);
            System.out.println("Trainee registered successfully. Username: " + trainee.getUsername());

        } catch (Exception e) {
            System.out.println("Failed to register trainee: " + e.getMessage());
        }
    }

    private void registerTrainer() {
        try {
            TrainerCreateDto dto = new TrainerCreateDto();

            System.out.print("First name: ");
            dto.setFirstName(scanner.nextLine());

            System.out.print("Last name: ");
            dto.setLastName(scanner.nextLine());

            System.out.print("Specialization: ");
            dto.setSpecialization(scanner.nextLine());

            TrainerResponseDto trainer = trainerService.create(dto);
            System.out.println("Trainer registered successfully. Username: " + trainer.getUsername());

        } catch (Exception e) {
            System.out.println("Failed to register trainer: " + e.getMessage());
        }
    }

    private void login(Role role) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            if (role == Role.TRAINEE) {
                traineeService.validateCredentials(username, password);
            } else {
                trainerService.validateCredentials(username, password);
            }

            authService.authenticate(username, role);
            System.out.println("Logged in as " + username + " (" + role + ")");

            if (role == Role.TRAINEE) {
                traineeMenu(username);
            } else {
                trainerMenu(username);
            }

            authService.logout();

        } catch (RuntimeException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void traineeMenu(String username) {
        while (true) {
            System.out.println("\n--- Trainee Menu ---");
            System.out.println("1. View profile");
            System.out.println("2. Update profile");
            System.out.println("3. Change password");
            System.out.println("4. View trainings");
            System.out.println("5. Change Active status");
            System.out.println("6. Delete account");
            System.out.println("7. View my trainers");
            System.out.println("8. Logout");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> viewTraineeProfile(username);
                case "2" -> updateTraineeProfile(username);
                case "3" -> changeTraineePassword(username);
                case "4" -> viewTraineeTrainings(username);
                case "5" -> toggleActivateStatusTrainee(username);
                case "6" -> deleteTrainee(username);
                case "7" -> viewMyTrainers(username);
                case "8" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void trainerMenu(String username) {
        while (true) {
            System.out.println("\n--- Trainer Menu ---");
            System.out.println("1. View profile");
            System.out.println("2. Update profile");
            System.out.println("3. Change password");
            System.out.println("4. View trainings");
            System.out.println("5. Add training");
            System.out.println("6. Manage trainees");
            System.out.println("7. View unassigned trainers for trainee");
            System.out.println("8. Change Active status");
            System.out.println("9. Delete account");
            System.out.println("10. View my trainees");
            System.out.println("11. Logout");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> viewTrainerProfile(username);
                case "2" -> updateTrainerProfile(username);
                case "3" -> changeTrainerPassword(username);
                case "4" -> viewTrainerTrainings(username);
                case "5" -> addTraining(username);
                case "6" -> manageTrainees(username);
                case "7" -> showUnassignedTrainers();
                case "8" -> toggleActivateStatusTrainer(username);
                case "9" -> deleteTrainer(username);
                case "10" -> viewMyTrainees(username);
                case "11" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewTraineeProfile(String username) {
        System.out.println(traineeService.getByUsername(username));
    }

    private void updateTraineeProfile(String username) {
        TraineeUpdateDto dto = new TraineeUpdateDto();
        System.out.print("New first name (leave blank to skip): ");
        String firstName = scanner.nextLine();
        if (!firstName.isBlank()) dto.setFirstName(firstName);

        System.out.print("New last name (leave blank to skip): ");
        String lastName = scanner.nextLine();
        if (!lastName.isBlank()) dto.setLastName(lastName);


        traineeService.update(username, dto);
        System.out.println("Profile updated.");
    }

    private void changeTraineePassword(String username) {
        System.out.print("New password: ");
        String newPassword = scanner.nextLine();
        traineeService.changePassword(username, newPassword);
        System.out.println("Password changed.");
    }

    private void viewTraineeTrainings(String username) {
        System.out.println("Filter trainings: ");
        TraineeTrainingFilterDto filterDto = new TraineeTrainingFilterDto();

        System.out.print("From date (YYYY-MM-DD) or blank: ");
            String from = scanner.nextLine();
            if (!from.isBlank()) filterDto.setFromDate(LocalDate.parse(from));

            System.out.print("To date (YYYY-MM-DD) or blank: ");
            String to = scanner.nextLine();
            if (!to.isBlank()) filterDto.setToDate(LocalDate.parse(to));

            System.out.print("Trainer name or blank: ");
            String trainerName = scanner.nextLine();
            if (!trainerName.isBlank()) filterDto.setTrainerName(trainerName);

            System.out.print("Training type or blank: ");
            String trainingType = scanner.nextLine();
            if (!trainingType.isBlank()) filterDto.setTrainingTypeName(trainingType);


        List<TrainingResponseDto> trainings = traineeService.getTrainings(username, filterDto);
        if (trainings.isEmpty()) {
            System.out.println("No trainings found.");
        } else {
            trainings.forEach(System.out::println);
        }
    }


    private void viewTrainerProfile(String username) {
        var trainer = trainerService.getByUsername(username);
        System.out.println(trainer);
    }

    private void updateTrainerProfile(String username) {
        var dto = new TrainerUpdateDto();

        System.out.print("New first name (leave blank to skip): ");
        String firstName = scanner.nextLine();
        if (!firstName.isBlank()) dto.setFirstName(firstName);

        System.out.print("New last name (leave blank to skip): ");
        String lastName = scanner.nextLine();
        if (!lastName.isBlank()) dto.setLastName(lastName);

        System.out.print("New specialization (leave blank to skip): ");
        String specialization = scanner.nextLine();
        if (!specialization.isBlank()) dto.setSpecialization(specialization);

        trainerService.update(username, dto);
        System.out.println("Trainer profile updated.");
    }

    private void changeTrainerPassword(String username) {
        System.out.print("New password: ");
        String newPassword = scanner.nextLine();
        trainerService.changePassword(username, newPassword);
        System.out.println("Password changed.");
    }

    private void viewTrainerTrainings(String username) {
        System.out.println("Filter trainings: ");
        var filterDto = new TrainerTrainingFilterDto();

            System.out.print("From date (YYYY-MM-DD) or blank: ");
            String from = scanner.nextLine();
            if (!from.isBlank()) filterDto.setFromDate(LocalDate.parse(from));

            System.out.print("To date (YYYY-MM-DD) or blank: ");
            String to = scanner.nextLine();
            if (!to.isBlank()) filterDto.setToDate(LocalDate.parse(to));

            System.out.print("Trainee name or blank: ");
            String traineeName = scanner.nextLine();
            if (!traineeName.isBlank()) filterDto.setTraineeName(traineeName);


        var trainings = trainerService.getTrainings(username, filterDto);
        if (trainings.isEmpty()) {
            System.out.println("No trainings found.");
        } else {
            trainings.forEach(System.out::println);
        }
    }



    private void addTraining(String username) {
        try {
            var dto = new TrainingCreateDto();
            dto.setTrainerUsername(username);

            System.out.print("Trainee username: ");
            dto.setTraineeUsername(scanner.nextLine());

            System.out.print("Training type ID: ");
            dto.setTrainingTypeId(Long.parseLong(scanner.nextLine()));

            System.out.print("Training name: ");
            dto.setTrainingName(scanner.nextLine());

            System.out.print("Training date (YYYY-MM-DD): ");
            dto.setTrainingDate(LocalDate.parse(scanner.nextLine()));

            System.out.print("Duration (minutes): ");
            dto.setTrainingDuration(Integer.parseInt(scanner.nextLine()));

            var training = trainingService.create(dto);
            System.out.println("Training added: " + training);
        } catch (Exception e) {
            System.out.println("Failed to add training: " + e.getMessage());
        }
    }
    private void manageTrainees(String username) {
        System.out.print("Enter trainee username to update trainers list: ");
        String traineeUsername = scanner.nextLine();

        var dto = new TraineeTrainersUpdateDto();
        System.out.println("Enter trainer usernames separated by comma:");
        String trainersInput = scanner.nextLine();
        dto.setTrainerUsernames(
                Arrays.stream(trainersInput.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList()
        );

        var updatedList = trainerService.updateTraineeTrainers(traineeUsername, dto);
        System.out.println("Updated trainers list:");
        updatedList.forEach(System.out::println);
    }

    private void toggleActivateStatusTrainee(String username) {
        var updated = traineeService.toggleActiveStatus(username);
        System.out.println("Account status changed: " + updated.isActive());
    }

    private void toggleActivateStatusTrainer(String username) {
        var updated = trainerService.toggleActiveStatus(username);
        System.out.println("Account status changed: " + updated.isActive());
    }

    private void deleteTrainee(String username) {
        traineeService.deleteByUsername(username);
        System.out.println("Account deleted successfully.");

        authService.logout();
        throw new RuntimeException("Account deleted. Logging out...");
    }

    private void deleteTrainer(String username) {
        trainerService.deleteByUsername(username);
        System.out.println("Account deleted successfully.");

        authService.logout();
        throw new RuntimeException("Account deleted. Logging out...");
    }

    private void showUnassignedTrainers() {
        System.out.print("Enter trainee username: ");
        String traineeUsername = scanner.nextLine();

        try {
            List<TrainerResponseDto> trainers =
                    trainerService.getUnassignedTrainersByTraineeUsername(traineeUsername);

            if (trainers.isEmpty()) {
                System.out.println("No unassigned trainers found.");
            } else {
                System.out.println("Unassigned trainers:");
                trainers.forEach(System.out::println);
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewMyTrainers(String username) {

        var trainers = traineeService.getTrainers(username);

        if (trainers.isEmpty()) {
            System.out.println("You have no assigned trainers.");
        } else {
            System.out.println("Your trainers:");
            trainers.forEach(System.out::println);
        }
    }

    private void viewMyTrainees(String username) {

        var trainees = trainerService.getTrainees(username);

        if (trainees.isEmpty()) {
            System.out.println("You have no assigned trainees.");
        } else {
            System.out.println("Your trainees:");
            trainees.forEach(System.out::println);
        }
    }
}