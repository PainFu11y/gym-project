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

    public void addDuration(int year, int month, int duration) {
        years.computeIfAbsent(year, y -> new HashMap<>())
                .merge(month, duration, Integer::sum);
    }

    public void subtractDuration(int year, int month, int duration) {
        Map<Integer, Integer> months = years.get(year);
        if (months == null) return;
        months.merge(month, -duration, Integer::sum);
        months.entrySet().removeIf(e -> e.getValue() <= 0);
        if (months.isEmpty()) years.remove(year);
    }
}
