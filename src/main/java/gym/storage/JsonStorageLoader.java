package gym.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gym.model.Trainee;
import gym.model.Trainer;
import gym.model.Training;
import gym.model.TrainingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class JsonStorageLoader implements InitializingBean {

    private static final Logger logger =
            LoggerFactory.getLogger(JsonStorageLoader.class);

    private final InMemoryStorage storage;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${data.trainee-file}")
    private String traineeFile;

    @Value("${data.trainer-file}")
    private String trainerFile;

    @Value("${data.training-file}")
    private String trainingFile;

    @Value("${data.trainingtype-file}")
    private String trainingTypeFile;

    public JsonStorageLoader(InMemoryStorage storage) {
        this.storage = storage;
    }

    @Override
    public void afterPropertiesSet() {
        logger.info("Starting JSON storage loading...");
        read(traineeFile, new TypeReference<List<Trainee>>() {
                },
                storage.getTrainees(), "Trainees");
        read(trainerFile, new TypeReference<List<Trainer>>() {
                },
                storage.getTrainers(), "Trainers");
        read(trainingFile, new TypeReference<List<Training>>() {
                },
                storage.getTrainings(), "Trainings");
        read(trainingTypeFile, new TypeReference<List<TrainingType>>() {
                },
                storage.getTrainingTypes(), "TrainingTypes");
        logger.info("JSON storage loading finished successfully");
    }

    private <T> void read(String file, TypeReference<List<T>> type,
                          List<T> target, String entityName) {
        try {
            Path path = Path.of(file);
            if (!Files.exists(path)) {
                logger.warn("{} file not found: {}. Skipping loading.", entityName, file);
                return;
            }

            List<T> data = mapper.readValue(path.toFile(), type);
            target.addAll(data);

            logger.info("Loaded {} {} from {}",
                    data.size(), entityName, file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + file, e);
        }
    }
}
