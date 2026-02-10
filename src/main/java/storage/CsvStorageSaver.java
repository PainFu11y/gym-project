package storage;

import model.Trainee;
import model.Trainer;
import model.Training;
import model.TrainingType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
//TODO realize saving
@Component
public class CsvStorageSaver {

    private final InMemoryStorage storage;

    @Value("${data.trainee-output}")
    private String traineeFile;

    @Value("${data.trainer-output}")
    private String trainerFile;

    @Value("${data.training-output}")
    private String trainingFile;

    @Value("${data.trainingtype-output}")
    private String trainingTypeFile;

    public CsvStorageSaver(InMemoryStorage storage) {
        this.storage = storage;
    }

    @PreDestroy
    public void saveAll() {
        saveTrainees();
        saveTrainers();
        saveTrainings();
        saveTrainingTypes();
    }

    private void saveTrainees() {
        try (BufferedWriter bw = Files.newBufferedWriter(
                Path.of(traineeFile), StandardCharsets.UTF_8)) {

            for (Trainee t : storage.getTrainees()) {
                bw.write(
                        t.getId() + "," +
                                t.getFirstName() + "," +
                                t.getLastName() + "," +
                                t.getUsername()
                );
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveTrainers() {
        try (BufferedWriter bw = Files.newBufferedWriter(
                Path.of(trainerFile), StandardCharsets.UTF_8)) {

            for (Trainer t : storage.getTrainers()) {
                bw.write(
                        t.getId() + "," +
                                t.getFirstName() + "," +
                                t.getLastName() + "," +
                                t.getUsername()
                );
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveTrainings() {
        try (BufferedWriter bw = Files.newBufferedWriter(
                Path.of(trainingFile), StandardCharsets.UTF_8)) {

            for (Training t : storage.getTrainings()) {
                bw.write(
                        t.getId() + "," +
                                t.getTraineeId() + "," +
                                t.getTrainerId() + "," +
                                t.getTrainingTypeId() + "," +
                                t.getTrainingName()
                );
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveTrainingTypes() {
        try (BufferedWriter bw = Files.newBufferedWriter(
                Path.of(trainingTypeFile), StandardCharsets.UTF_8)) {

            for (TrainingType t : storage.getTrainingTypes()) {
                bw.write(
                        t.getId() + "," +
                                t.getName().name()
                );
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
