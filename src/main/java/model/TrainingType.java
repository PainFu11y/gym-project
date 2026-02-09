package model;

import model.enums.TrainingTypeName;

public class TrainingType {
    private Long id;

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

    private TrainingTypeName name;
}
