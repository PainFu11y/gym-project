package com.gym_project.actuator.metrics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class MetricsScheduler {

    private final GymMetrics gymMetrics;

    @Scheduled(fixedDelay = 60_000)
    public void refreshMetrics() {
        log.debug("Refreshing Prometheus gauge metrics from database");
        gymMetrics.refreshGauges();
    }
}