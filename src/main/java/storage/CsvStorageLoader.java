package storage;

import model.Trainee;
import model.Trainer;
import model.Training;
import model.TrainingType;
import model.enums.TrainingTypeName;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
//TODO realize loading
@Component
public class CsvStorageLoader implements InitializingBean {

    private final InMemoryStorage storage;

    @Value("${data.trainee-file}")
    private Resource traineeFile;

    @Value("${data.trainer-file}")
    private Resource trainerFile;

    @Value("${data.training-file}")
    private Resource trainingFile;

    @Value("${data.trainingtype-file}")
    private Resource trainingTypeFile;

    public CsvStorageLoader(InMemoryStorage storage) {
        this.storage = storage;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        loadTrainees();
        loadTrainers();
        loadTrainings();
        loadTrainingTypes();
    }

    private void loadTrainees() {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(traineeFile.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                Trainee trainee = new Trainee();
                trainee.setId(Long.parseLong(fields[0]));
                trainee.setFirstName(fields[1]);
                trainee.setLastName(fields[2]);
                trainee.setUsername(fields[3]);
                storage.getTrainees().add(trainee);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTrainers() {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(trainerFile.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                Trainer trainer = new Trainer();
                trainer.setId(Long.parseLong(fields[0]));
                trainer.setFirstName(fields[1]);
                trainer.setLastName(fields[2]);
                trainer.setUsername(fields[3]);
                storage.getTrainers().add(trainer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTrainings() {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(trainingFile.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                Training training = new Training();
                training.setId(Long.parseLong(fields[0]));
                training.setTraineeId(Long.parseLong(fields[1]));
                training.setTrainerId(Long.parseLong(fields[2]));
                training.setTrainingTypeId(Long.parseLong(fields[3]));
                training.setTrainingName(fields[4]);
                storage.getTrainings().add(training);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTrainingTypes() {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(trainingTypeFile.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                TrainingType type = new TrainingType();
                type.setId(Long.parseLong(fields[0]));
                type.setName(TrainingTypeName.valueOf(fields[1].toUpperCase()));
                storage.getTrainingTypes().add(type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
