package com.gym_report.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkloadMessage implements Serializable {

    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;

    @JsonProperty("isActive")
    private boolean isActive;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate trainingDate;

    private int trainingDuration;
    private ActionType actionType;

    private String transactionId;

    public enum ActionType {
        ADD, DELETE
    }
}
