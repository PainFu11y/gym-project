package gym.model;

import gym.model.enums.TrainingTypeName;

public class TrainingType {
    private Long id;
    private TrainingTypeName name;

    public TrainingTypeName getName() {
        return name;
    }

    public void setName(TrainingTypeName name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
