package com.gym_report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkloadSummary {

    private String username;
    private String firstName;
    private String lastName;
    private boolean isActive;

    private Map<Integer, Map<Integer, Integer>> years = new HashMap<>();
}
