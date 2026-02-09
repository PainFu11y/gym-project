package Utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class PasswordGenerator {

    private static final String CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public char[] generate() {
        SecureRandom random = new SecureRandom();
        char[] password = new char[10];
        for (int i = 0; i < password.length; i++) {
            password[i] = CHARS.charAt(random.nextInt(CHARS.length()));
        }
        return password;
    }
}
