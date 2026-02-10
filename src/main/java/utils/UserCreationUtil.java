package utils;

import dao.TraineeDao;
import dao.TrainerDao;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class UserCreationUtil {

    private final TrainerDao trainerDao;
    private final TraineeDao traineeDao;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    @Autowired
    public UserCreationUtil(TrainerDao trainerDao,
                            TraineeDao traineeDao,
                            UsernameGenerator usernameGenerator,
                            PasswordGenerator passwordGenerator) {
        this.trainerDao = trainerDao;
        this.traineeDao = traineeDao;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
    }


    public void assignUsernameAndPassword(User user) {
        validate(user);

        Set<String> existingUsernames = Stream.concat(
                        trainerDao.findAll().stream(),
                        traineeDao.findAll().stream()
                )
                .map(User::getUsername)
                .collect(Collectors.toSet());

        user.setUsername(
                usernameGenerator.generate(
                        user.getFirstName(),
                        user.getLastName(),
                        existingUsernames
                )
        );

        user.setPassword(passwordGenerator.generate());
    }

    private void validate(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }

        if (user.getFirstName() == null || user.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }

        if (user.getLastName() == null || user.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }

        if (user.getUsername() != null) {
            throw new IllegalStateException("Username is already set");
        }

        if (user.getPassword() != null) {
            throw new IllegalStateException("Password is already set");
        }
    }
}
