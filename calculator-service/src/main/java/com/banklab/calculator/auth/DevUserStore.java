package com.banklab.calculator.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Demo users for the DEV LOGIN STAND-IN.
 *
 * The real login belongs to Group 10. Until their service is running, these
 * users let you demonstrate "login page with FD Calculator integrated" (Lab L14).
 * Passwords are stored only as BCrypt hashes (Lab L1/L2 practice).
 *
 *   alice123 / Alice@123   CUSTOMER      categories SENIOR + EMPLOYEE      (L11 row 1)
 *   ravi     / Ravi@1234   CUSTOMER      categories EMPLOYEE + PREMIUM_USER (L11 row 2, capped)
 *   bob      / Bob@12345   CUSTOMER      category  GENERAL                 (no bonus)
 *   officer1 / Officer@123 BANK_OFFICER  -> must get 403 on the calculator (Lab L10)
 */
@Component
public class DevUserStore {

    public record DevUser(String username, String passwordHash, String role, List<String> categories) {
    }

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final Map<String, DevUser> users = new LinkedHashMap<>();

    public DevUserStore() {
        add("alice123", "Alice@123", "CUSTOMER", List.of("SENIOR", "EMPLOYEE"));
        add("ravi", "Ravi@1234", "CUSTOMER", List.of("EMPLOYEE", "PREMIUM_USER"));
        add("bob", "Bob@12345", "CUSTOMER", List.of("GENERAL"));
        add("officer1", "Officer@123", "BANK_OFFICER", List.of());
    }

    private void add(String username, String password, String role, List<String> categories) {
        users.put(username.toLowerCase(Locale.ROOT),
                new DevUser(username, encoder.encode(password), role, categories));
    }

    /** Returns the user if the username exists and the password matches its BCrypt hash. */
    public Optional<DevUser> authenticate(String username, String password) {
        if (username == null || password == null) {
            return Optional.empty();
        }
        DevUser user = users.get(username.trim().toLowerCase(Locale.ROOT));
        if (user != null && encoder.matches(password, user.passwordHash())) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public Optional<DevUser> find(String username) {
        return username == null ? Optional.empty()
                : Optional.ofNullable(users.get(username.trim().toLowerCase(Locale.ROOT)));
    }

    public List<String> categoriesOf(String username) {
        return find(username).map(DevUser::categories).orElse(List.of());
    }
}
