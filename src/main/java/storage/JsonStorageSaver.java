package storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.nio.file.Path;

@Component
public class JsonStorageSaver {

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
        write(traineeFile, storage.getTrainees());
        write(trainerFile, storage.getTrainers());
        write(trainingFile, storage.getTrainings());
        write(trainingTypeFile, storage.getTrainingTypes());
    }

    private void write(String file, Object data) {
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(Path.of(file).toFile(), data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save " + file, e);
        }
    }
}
