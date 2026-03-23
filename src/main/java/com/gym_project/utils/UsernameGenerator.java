package com.gym_project.utils;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsernameGenerator {

    public static String generate(String firstName, String lastName, List<String> existingUsernames) {
        String base = firstName + "." + lastName;

        int maxIndex = existingUsernames.stream()
                .filter(name -> name.startsWith(base))
                .map(name -> {
                    String suffix = name.substring(base.length());
                    if (suffix.isEmpty()) return 0;
                    try {
                        return Integer.parseInt(suffix);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .max(Integer::compare)
                .orElse(-1);

        return maxIndex < 0 ? base : base + (maxIndex + 1);
    }
}
