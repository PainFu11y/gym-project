package com.gym_project.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Service
public class LoginAttemptService {
    @Value("${security.login.max-attempts}")
    private static int maxAttempts;

    @Value("${security.login.block-duration-seconds}")
    private long blockDurationSeconds;

    private final ConcurrentHashMap<String, AttemptRecord> attempts = new ConcurrentHashMap<>();



    public void recordFailure(String username) {
        attempts.compute(username, (u, record) -> {
            if (record == null) {
                record = new AttemptRecord();
            }
            record.increment();
            return record;
        });

        AttemptRecord record = attempts.get(username);
        if (record.getCount() >= maxAttempts) {
            log.warn("Account locked due to {} failed attempts: username='{}'",
                    maxAttempts, username);
        } else {
            log.debug("Failed attempt {}/{} for username='{}'",
                    record.getCount(), maxAttempts, username);
        }
    }


    public void recordSuccess(String username) {
        attempts.remove(username);
        log.debug("Cleared failed attempts for username='{}'", username);
    }


    public boolean isBlocked(String username) {
        AttemptRecord record = attempts.get(username);
        if (record == null) {
            return false;
        }

        if (record.getCount() < maxAttempts) {
            return false;
        }

        long secondsSinceLock = Instant.now().getEpochSecond()
                - record.getLockTime().getEpochSecond();

        if (secondsSinceLock >= blockDurationSeconds) {
            attempts.remove(username);
            log.info("Lock expired for username='{}', resetting attempts", username);
            return false;
        }

        long remainingSeconds = blockDurationSeconds - secondsSinceLock;
        log.warn("Blocked login attempt for username='{}', {} seconds remaining",
                username, remainingSeconds);
        return true;
    }

    static class AttemptRecord {

        private int     count    = 0;
        private Instant lockTime = null;

        void increment() {
            count++;
            if (count == maxAttempts) {
                lockTime = Instant.now();
            }
        }

        int     getCount()    { return count; }
        Instant getLockTime() { return lockTime; }
    }
}