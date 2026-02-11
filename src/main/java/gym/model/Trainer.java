package gym.model;

public class Trainer extends User {
    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    private String specialization;
}
