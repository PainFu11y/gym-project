package com.gym_project.security;

import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.repository.TraineeRepository;
import com.gym_project.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GymUserDetailsService implements UserDetailsService {

    private final TrainerRepository   trainerRepository;
    private final TraineeRepository   traineeRepository;
    private final LoginAttemptService loginAttemptService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username='{}'", username);

        boolean nonLocked = !loginAttemptService.isBlocked(username);

        Optional<Trainer> trainer = trainerRepository.findByUsername(username);
        if (trainer.isPresent()) {
            Trainer t = trainer.get();
            log.debug("Found trainer username='{}'", username);
            return new GymUserDetails(t.getUsername(), t.getPassword(), Role.TRAINER, nonLocked);
        }

        Optional<Trainee> trainee = traineeRepository.findByUsername(username);
        if (trainee.isPresent()) {
            Trainee t = trainee.get();
            log.debug("Found trainee username='{}'", username);
            return new GymUserDetails(t.getUsername(), t.getPassword(), Role.TRAINEE, nonLocked);
        }

        log.warn("No user found for username='{}'", username);
        throw new UsernameNotFoundException("User not found: " + username);
    }
}