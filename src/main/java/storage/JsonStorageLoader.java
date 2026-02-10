package storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class JsonStorageLoader implements InitializingBean {

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
        read(traineeFile, new TypeReference<List<Trainee>>() {}, storage.getTrainees());
        read(trainerFile, new TypeReference<List<Trainer>>() {}, storage.getTrainers());
        read(trainingFile, new TypeReference<List<Training>>() {}, storage.getTrainings());
        read(trainingTypeFile, new TypeReference<List<TrainingType>>() {}, storage.getTrainingTypes());
    }

    private <T> void read(String file, TypeReference<List<T>> type, List<T> target) {
        try {
            Path path = Path.of(file);
            if (!Files.exists(path)) return;

            List<T> data = mapper.readValue(path.toFile(), type);
            target.addAll(data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + file, e);
        }
    }
}
