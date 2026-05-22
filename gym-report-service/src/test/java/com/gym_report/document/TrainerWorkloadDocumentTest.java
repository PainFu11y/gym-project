package com.gym_report.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class TrainerWorkloadDocumentTest {

    private TrainerWorkloadDocument doc;

    @BeforeEach
    void setUp() {
        doc = TrainerWorkloadDocument.builder()
                .username("John.Smith")
                .firstName("John")
                .lastName("Smith")
                .isActive(true)
                .years(new HashMap<>())
                .build();
    }

    @Test
    void addDuration_shouldCreateYearAndMonthEntry_whenNoneExists() {
        doc.addDuration(2025, 4, 60);

        assertThat(doc.getYears()).containsKey(2025);
        assertThat(doc.getYears().get(2025)).containsEntry(4, 60);
    }

    @Test
    void addDuration_shouldAccumulateMinutes_forSameYearAndMonth() {
        doc.addDuration(2025, 4, 60);
        doc.addDuration(2025, 4, 45);

        assertThat(doc.getYears().get(2025).get(4)).isEqualTo(105);
    }

    @Test
    void addDuration_shouldTrackMultipleMonthsIndependently() {
        doc.addDuration(2025, 4, 60);
        doc.addDuration(2025, 5, 90);

        assertThat(doc.getYears().get(2025)).containsEntry(4, 60);
        assertThat(doc.getYears().get(2025)).containsEntry(5, 90);
    }

    @Test
    void addDuration_shouldTrackMultipleYearsIndependently() {
        doc.addDuration(2024, 12, 60);
        doc.addDuration(2025, 1, 90);

        assertThat(doc.getYears()).containsKey(2024);
        assertThat(doc.getYears()).containsKey(2025);
        assertThat(doc.getYears().get(2024).get(12)).isEqualTo(60);
        assertThat(doc.getYears().get(2025).get(1)).isEqualTo(90);
    }

    @Test
    void subtractDuration_shouldReduceMinutes_whenPartialSubtraction() {
        doc.addDuration(2025, 4, 90);
        doc.subtractDuration(2025, 4, 30);

        assertThat(doc.getYears().get(2025).get(4)).isEqualTo(60);
    }

    @Test
    void subtractDuration_shouldRemoveMonthEntry_whenMinutesDropToZero() {
        doc.addDuration(2025, 4, 60);
        doc.subtractDuration(2025, 4, 60);

        assertThat(doc.getYears()).doesNotContainKey(2025);
    }

    @Test
    void subtractDuration_shouldRemoveMonthEntry_whenMinutesDropBelowZero() {
        doc.addDuration(2025, 4, 30);
        doc.subtractDuration(2025, 4, 60);

        assertThat(doc.getYears()).doesNotContainKey(2025);
    }

    @Test
    void subtractDuration_shouldRemoveYearEntry_whenAllMonthsAreGone() {
        doc.addDuration(2025, 3, 60);
        doc.addDuration(2025, 4, 90);

        doc.subtractDuration(2025, 3, 60);
        doc.subtractDuration(2025, 4, 90);

        assertThat(doc.getYears()).doesNotContainKey(2025);
    }

    @Test
    void subtractDuration_shouldBeNoOp_whenYearDoesNotExist() {
        doc.addDuration(2025, 4, 60);

        doc.subtractDuration(2099, 1, 60);

        assertThat(doc.getYears()).doesNotContainKey(2099);
        assertThat(doc.getYears()).containsKey(2025);
    }

    @Test
    void subtractDuration_shouldOnlyAffectTargetMonth() {
        doc.addDuration(2025, 4, 60);
        doc.addDuration(2025, 5, 90);

        doc.subtractDuration(2025, 4, 60);

        assertThat(doc.getYears().get(2025)).doesNotContainKey(4);
        assertThat(doc.getYears().get(2025)).containsEntry(5, 90);
    }

    @Test
    void subtractDuration_shouldOnlyAffectTargetYear() {
        doc.addDuration(2024, 12, 60);
        doc.addDuration(2025, 4, 60);

        doc.subtractDuration(2024, 12, 60);

        assertThat(doc.getYears()).doesNotContainKey(2024);
        assertThat(doc.getYears()).containsKey(2025);
    }
}
