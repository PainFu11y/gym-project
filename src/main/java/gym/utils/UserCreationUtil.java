package gym.utils;

import gym.dao.TraineeDao;
import gym.dao.TrainerDao;
import gym.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class UserCreationUtil {

    private final TrainerDao trainerDao;
    private final TraineeDao traineeDao;
    private final UsernameGenerator usernameGenerator;
    private final PasswordHasher passwordHasher;

    @Autowired
    public UserCreationUtil(TrainerDao trainerDao,
                            TraineeDao traineeDao,
                            UsernameGenerator usernameGenerator,
                            PasswordHasher passwordHasher) {
        this.trainerDao = trainerDao;
        this.traineeDao = traineeDao;
        this.usernameGenerator = usernameGenerator;
        this.passwordHasher = passwordHasher;
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

        char[] rawPassword = user.getPassword();
        user.setPassword(passwordHasher.hash(rawPassword));
        Arrays.fill(rawPassword, '\0');
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
    }
}
