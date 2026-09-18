package com.surya.productservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class AdminPasswordService {

    private final String expectedHash;

    public AdminPasswordService(
            @Value("${ADMIN_PASSWORD_SHA256:}") String expectedHash) {
        this.expectedHash = expectedHash.trim().toLowerCase();
    }

    public boolean isValid(String password) {
        if (expectedHash.isBlank() || password == null || password.isBlank()) {
            return false;
        }

        String actualHash = sha256(password);

        return MessageDigest.isEqual(
                expectedHash.getBytes(StandardCharsets.UTF_8),
                actualHash.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(
                    digest.digest(value.getBytes(StandardCharsets.UTF_8))
            );
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable.", exception);
        }
    }
}
