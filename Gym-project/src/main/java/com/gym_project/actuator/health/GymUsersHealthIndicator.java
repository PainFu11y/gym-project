package com.gym_project.actuator.health;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Slf4j
@Component("gymUsers")
public class GymUsersHealthIndicator implements HealthIndicator {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Health health() {
        try {
            Long activeTrainees = (Long) entityManager
                    .createQuery("SELECT COUNT(t) FROM Trainee t WHERE t.isActive = true")
                    .getSingleResult();

            Long activeTrainers = (Long) entityManager
                    .createQuery("SELECT COUNT(t) FROM Trainer t WHERE t.isActive = true")
                    .getSingleResult();

            boolean healthy = activeTrainees > 0 || activeTrainers > 0;

            Health.Builder builder = healthy ? Health.up() : Health.down()
                    .withDetail("reason", "No active trainees or trainers found");

            return builder
                    .withDetail("activeTrainees", activeTrainees)
                    .withDetail("activeTrainers", activeTrainers)
                    .build();

        } catch (Exception e) {
            log.error("Gym users health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}