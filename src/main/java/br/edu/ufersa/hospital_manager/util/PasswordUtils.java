package br.edu.ufersa.hospital_manager.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordUtils {
    private PasswordUtils() {
    }

    public static String hash(String password) {
        if (password == null || password.isBlank()) {
            throw new RuntimeException("Password cannot be null or empty.");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();

            for (byte hashedByte : hashedBytes) {
                builder.append(String.format("%02x", hashedByte));
            }

            return builder.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new RuntimeException("Unable to hash password.", exception);
        }
    }

    public static boolean matches(String rawPassword, String hashedPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            return false;
        }

        if (hashedPassword == null || hashedPassword.isBlank()) {
            return false;
        }

        return hash(rawPassword).equals(hashedPassword);
    }
}