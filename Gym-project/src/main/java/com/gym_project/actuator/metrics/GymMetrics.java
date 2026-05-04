package com.gym_project.actuator.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class GymMetrics {

    private final Counter loginSuccessCounter;
    private final Counter loginFailureCounter;
    private final Counter traineeRegistrationCounter;
    private final Counter trainerRegistrationCounter;
    private final Counter trainingsCreatedCounter;

    private final Timer trainingCreationTimer;

    private final AtomicLong activeTraineesCount = new AtomicLong(0);
    private final AtomicLong activeTrainersCount  = new AtomicLong(0);

    @PersistenceContext
    private EntityManager entityManager;

    public GymMetrics(MeterRegistry registry) {

        loginSuccessCounter = Counter.builder("gym.logins")
                .tag("result", "success")
                .description("Total successful login attempts")
                .register(registry);

        loginFailureCounter = Counter.builder("gym.logins")
                .tag("result", "failure")
                .description("Total failed login attempts")
                .register(registry);

        traineeRegistrationCounter = Counter.builder("gym.registrations")
                .tag("type", "trainee")
                .description("Total new trainee registrations")
                .register(registry);

        trainerRegistrationCounter = Counter.builder("gym.registrations")
                .tag("type", "trainer")
                .description("Total new trainer registrations")
                .register(registry);

        trainingsCreatedCounter = Counter.builder("gym.trainings.created")
                .description("Total training sessions created")
                .register(registry);

        trainingCreationTimer = Timer.builder("gym.training.creation.duration")
                .description("Time taken to create a training session")
                .register(registry);

        Gauge.builder("gym.active.trainees", activeTraineesCount, AtomicLong::get)
                .description("Current number of active trainees")
                .register(registry);

        Gauge.builder("gym.active.trainers", activeTrainersCount, AtomicLong::get)
                .description("Current number of active trainers")
                .register(registry);
    }

    public void recordLoginSuccess() {
        loginSuccessCounter.increment();
    }

    public void recordLoginFailure() {
        loginFailureCounter.increment();
    }

    public void recordTraineeRegistration() {
        traineeRegistrationCounter.increment();
        activeTraineesCount.incrementAndGet();
    }

    public void recordTrainerRegistration() {
        trainerRegistrationCounter.increment();
        activeTrainersCount.incrementAndGet();
    }

    public void recordTrainingCreated() {
        trainingsCreatedCounter.increment();
    }

    public Timer getTrainingCreationTimer() {
        return trainingCreationTimer;
    }

    public void refreshGauges() {
        try {
            Long trainees = (Long) entityManager
                    .createQuery("SELECT COUNT(t) FROM Trainee t WHERE t.isActive = true")
                    .getSingleResult();
            Long trainers = (Long) entityManager
                    .createQuery("SELECT COUNT(t) FROM Trainer t WHERE t.isActive = true")
                    .getSingleResult();

            activeTraineesCount.set(trainees);
            activeTrainersCount.set(trainers);
        } catch (Exception e) {
            log.error("Failed to refresh gauge metrics", e);
        }
    }
}