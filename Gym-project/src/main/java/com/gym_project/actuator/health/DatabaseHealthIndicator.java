package com.gym_project.actuator.health;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Slf4j
@Component("database")
public class DatabaseHealthIndicator implements HealthIndicator {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Health health() {
        try {
            Integer result = (Integer) entityManager
                    .createNativeQuery("SELECT 1")
                    .getSingleResult();

            return Health.up()
                    .withDetail("totalUsers", result)
                    .withDetail("status", "EntityManager query successful")
                    .build();

        } catch (Exception e) {
            log.error("Database health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}