package com.gym_project.client;

import com.gym_project.security.ServiceJwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportServiceClient {

    private final DiscoveryClient discoveryClient;
    private final ServiceJwtService serviceJwtService;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;
    private final RestTemplate restTemplate;

    @Value("${report.service.name}")
    private String reportServiceName;

    public void sendWorkload(ReportWorkloadRequest request, String transactionId) {
        circuitBreakerFactory.create("report-service").run(
                () -> doSendWorkload(request, transactionId),
                throwable -> handleFallback(request, throwable)
        );
    }

    private Void doSendWorkload(ReportWorkloadRequest request, String transactionId) {
        String url = resolveServiceUrl() + "/api/trainer-workload";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(serviceJwtService.generateServiceToken());
        if (transactionId != null) {
            headers.set("X-Transaction-Id", transactionId);
        }

        HttpEntity<ReportWorkloadRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<Void> response = restTemplate.postForEntity(url, entity, Void.class);

        log.info("Report service responded: {}", response.getStatusCode());
        return null;
    }

    private Void handleFallback(ReportWorkloadRequest request, Throwable t) {
        log.error("Circuit breaker open — report service unavailable. " +
                        "Workload update for trainer {} was not sent. Reason: {}",
                request.getTrainerUsername(), t.getMessage());
        return null;
    }

    private String resolveServiceUrl() {
        List<ServiceInstance> instances = discoveryClient.getInstances(reportServiceName);
        if (instances.isEmpty()) {
            throw new IllegalStateException("No instances of " + reportServiceName + " found in Eureka");
        }
        ServiceInstance instance = instances.get(0);
        return instance.getUri().toString();
    }
}
