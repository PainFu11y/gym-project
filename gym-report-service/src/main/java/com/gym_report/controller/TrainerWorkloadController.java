package com.gym_report.controller;

import com.gym_report.dto.TrainerWorkloadRequestDto;
import com.gym_report.model.TrainerWorkloadSummary;
import com.gym_report.service.TrainerWorkloadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/trainer-workload")
@RequiredArgsConstructor
@Slf4j
public class TrainerWorkloadController {

    private final TrainerWorkloadService workloadService;

    @PostMapping
    public ResponseEntity<Void> updateWorkload(
            @Valid @RequestBody TrainerWorkloadRequestDto dto,
            @RequestHeader(value = "X-Transaction-Id", required = false) String transactionId
    ) {
        String txId = transactionId != null ? transactionId : UUID.randomUUID().toString();
        log.info("[{}] POST /api/trainer-workload called for trainer: {}", txId, dto.getTrainerUsername());

        workloadService.processWorkload(dto, txId);

        log.info("[{}] POST /api/trainer-workload responded 200 OK", txId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerWorkloadSummary> getWorkload(
            @PathVariable String username,
            @RequestHeader(value = "X-Transaction-Id", required = false) String transactionId
    ) {
        String txId = transactionId != null ? transactionId : UUID.randomUUID().toString();
        log.info("[{}] GET /api/trainer-workload/{} called", txId, username);

        TrainerWorkloadSummary summary = workloadService.getWorkload(username);
        if (summary == null) {
            log.warn("[{}] Trainer {} not found", txId, username);
            return ResponseEntity.notFound().build();
        }

        log.info("[{}] GET /api/trainer-workload/{} responded 200 OK", txId, username);
        return ResponseEntity.ok(summary);
    }
}
