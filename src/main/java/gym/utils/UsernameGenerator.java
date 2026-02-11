package gym.utils;

import org.springframework.stereotype.Component;

import java.util.Set;


@Component
public class UsernameGenerator {

    public String generate(String firstName, String lastName, Set<String> existingUsernames) {
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

        if (maxIndex == -1) {
            return base;
        } else {
            int nextUsersIndex = maxIndex + 1;
            return base + (nextUsersIndex);
        }
    }
}

