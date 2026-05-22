package com.gym_report.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "trainer_workload")
@CompoundIndex(
        name  = "idx_firstName_lastName",
        def   = "{'firstName': 1, 'lastName': 1}"
)
public class TrainerWorkloadDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("username")
    private String username;

    @Field("firstName")
    private String firstName;

    @Field("lastName")
    private String lastName;

    @Field("isActive")
    private boolean isActive;

    @Builder.Default
    @Field("years")
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
