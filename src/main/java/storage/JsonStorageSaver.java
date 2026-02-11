package storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class JsonStorageSaver {
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

    public JsonStorageSaver(InMemoryStorage storage) {
        this.storage = storage;
    }

    @PreDestroy
    public void saveAll() {
        logger.info("Starting JSON storage saving...");

        write(traineeFile, storage.getTrainees(), "Trainees");
        write(trainerFile, storage.getTrainers(), "Trainers");
        write(trainingFile, storage.getTrainings(), "Trainings");
        write(trainingTypeFile, storage.getTrainingTypes(), "TrainingTypes");

        logger.info("JSON storage saving completed successfully");
    }

    private void write(String file, Object data, String entityName) {
        try {
            Path path = Path.of(file);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(path.toFile(), data);

            logger.info("Saved {} {} to {}",
                    ((java.util.Collection<?>) data).size(),
                    entityName,
                    file);

        } catch (Exception e) {
            logger.error("Failed to save {} to file {}",
                    entityName,
                    file,
                    e);
            throw new RuntimeException("Failed to save " + file, e);
        }
    }
}
