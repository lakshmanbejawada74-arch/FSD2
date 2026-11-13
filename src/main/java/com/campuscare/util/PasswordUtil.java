package com.campuscare.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility for BCrypt password hashing and strength validation.
 */
public class PasswordUtil {

    private static final String SPECIAL_CHARACTERS = "@$!%*?&^#()[]{}|;:,.<>~_-+=/\\\"'`";

    /**
     * Hashes a plain text password using BCrypt with log rounds 12.
     */
    public static String hashPassword(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        return BCrypt.hashpw(plainText, BCrypt.gensalt(12));
    }

    /**
     * Verifies a plain text password against a BCrypt hash.
     */
    public static boolean checkPassword(String plainText, String hashedPassword) {
        if (plainText == null || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainText, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if the password satisfies complexity rules:
     * At least 8 chars, uppercase, lowercase, digit, special character.
     */
    public static boolean isPasswordComplex(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (SPECIAL_CHARACTERS.indexOf(c) >= 0) {
                hasSpecial = true;
            }
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}
