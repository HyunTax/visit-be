package com.sht4873.reservation.core.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    private final PasswordEncoder passwordEncoder;

    public SecurityUtils(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String encode(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean matches(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }

    public boolean nonMatches(String password, String encodedPassword) {
        return !matches(password, encodedPassword);
    }
}
