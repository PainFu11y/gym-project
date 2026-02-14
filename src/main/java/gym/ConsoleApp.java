package gym;

import gym.facade.TrainingFacade;
import gym.model.Trainee;
import gym.model.Trainer;
import gym.model.Training;
import gym.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;

@Component
public class ConsoleApp {

    private final TrainingFacade facade;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleApp(TrainingFacade facade) {
        this.facade = facade;
    }

    public void start() {
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {

                case "1" -> {
                    Trainee trainee = readTrainee();
                    Trainee saved = facade.registerTrainee(trainee);
                }

                case "2" -> {
                    Long id = readId("Trainee ID");
                    Optional<Trainee> optional = facade.getTraineeById(id);

                    if (optional.isPresent()) {
                        Trainee trainee = optional.get();
                        fillUserData(trainee);
                        facade.updateTrainee(trainee);
                        System.out.println("Updated");
                    } else {
                        System.out.println("Not found");
                    }
                }

                case "3" -> {
                    Long id = readId("Trainee ID");
                    facade.deleteTrainee(id);
                    System.out.println("Deleted (if existed)");
                }

                case "4" -> {
                    Long id = readId("Trainee ID");
                    facade.getTraineeById(id)
                            .ifPresentOrElse(
                                    this::printUser,
                                    () -> System.out.println("Not found")
                            );
                }

                case "5" -> {
                    Trainer trainer = readTrainer();
                    Trainer saved = facade.registerTrainer(trainer);
                }

                case "6" -> {
                    Long id = readId("Trainer ID");
                    Optional<Trainer> optional = facade.getTrainerById(id);

                    if (optional.isPresent()) {
                        Trainer trainer = optional.get();
                        fillUserData(trainer);
                        facade.updateTrainer(trainer);
                        System.out.println("Updated");
                    } else {
                        System.out.println("Not found");
                    }
                }

                case "7" -> {
                    Long id = readId("Trainer ID");
                    facade.getTrainerById(id)
                            .ifPresentOrElse(
                                    this::printUser,
                                    () -> System.out.println("Not found")
                            );
                }

                case "8" -> {
                    Training training = readTraining();
                    Training saved = facade.scheduleTraining(training);
                    System.out.println("Training ID: " + saved.getId());
                }

                case "9" -> {
                    Long id = readId("Training ID");
                    facade.getTrainingById(id)
                            .ifPresentOrElse(
                                    t -> System.out.println(
                                            t.getId() + " " + t.getTrainingName()),
                                    () -> System.out.println("Not found")
                            );
                }

                case "10" -> facade.getAllTrainers()
                        .forEach(this::printUser);

                case "11" -> facade.getAllTrainings()
                        .forEach(t ->
                                System.out.println(t.getId() + " "
                                        + t.getTrainingName()));

                case "0" -> running = false;

                default -> System.out.println("Invalid option");
            }
        }
    }


    private void printMenu() {
        System.out.println("""
                
                1  Register Trainee
                2  Update Trainee
                3  Delete Trainee
                4  Find Trainee
                5  Register Trainer
                6  Update Trainer
                7  Find Trainer
                8  Schedule Training
                9  Find Training
                10 Show Trainers
                11 Show Trainings
                0  Exit
                """);
    }

    private Long readId(String label) {
        System.out.print(label + ": ");
        return Long.parseLong(scanner.nextLine());
    }

    private Trainee readTrainee() {
        Trainee trainee = new Trainee();
        fillUserData(trainee);
        return trainee;
    }

    private Trainer readTrainer() {
        Trainer trainer = new Trainer();
        fillUserData(trainer);
        return trainer;
    }

    private void fillUserData(gym.model.User user) {
        System.out.print("First name: ");
        user.setFirstName(scanner.nextLine());

        System.out.print("Last name: ");
        user.setLastName(scanner.nextLine());
    }

    private Training readTraining() {
        Training training = new Training();

        System.out.print("Trainee ID: ");
        training.setTraineeId(Long.parseLong(scanner.nextLine()));

        System.out.print("Trainer ID: ");
        training.setTrainerId(Long.parseLong(scanner.nextLine()));

        System.out.print("Training name: ");
        training.setTrainingName(scanner.nextLine());

        training.setTrainingDate(LocalDateTime.now());
        training.setDurationMinutes(60);
        training.setTrainingTypeId(1L);

        return training;
    }

    private void printUser(User user) {
        System.out.println(user.getId() + " "
                + user.getFirstName() + " "
                + user.getLastName() + " "
                + user.getUsername() + ", is active:" + user.isActive()
        );


    }
}