package com.campuscare;

import com.campuscare.util.PasswordUtil;
import com.campuscare.util.ValidationUtil;
import org.junit.Test;

import static org.junit.Assert.*;

public class ValidationAndSecurityTest {

    @Test
    public void testPasswordComplexity() {
        // Valid passwords
        assertTrue(PasswordUtil.isPasswordComplex("Admin@123"));
        assertTrue(PasswordUtil.isPasswordComplex("Campus#Care2026"));
        assertTrue(PasswordUtil.isPasswordComplex("Pass$word1"));

        // Invalid: missing special char
        assertFalse(PasswordUtil.isPasswordComplex("Admin1234"));
        // Invalid: missing digit
        assertFalse(PasswordUtil.isPasswordComplex("Admin@abcd"));
        // Invalid: missing uppercase
        assertFalse(PasswordUtil.isPasswordComplex("admin@123"));
        // Invalid: missing lowercase
        assertFalse(PasswordUtil.isPasswordComplex("ADMIN@123"));
        // Invalid: shorter than 8 chars
        assertFalse(PasswordUtil.isPasswordComplex("Adm@1"));
        // Null or empty
        assertFalse(PasswordUtil.isPasswordComplex(null));
        assertFalse(PasswordUtil.isPasswordComplex(""));
    }

    @Test
    public void testPasswordHashingAndVerification() {
        String plain = "Secret@2026";
        String hash = PasswordUtil.hashPassword(plain);

        assertNotNull(hash);
        assertTrue(hash.startsWith("$2a$12$") || hash.startsWith("$2a$"));
        assertTrue(PasswordUtil.checkPassword(plain, hash));
        assertFalse(PasswordUtil.checkPassword("Wrong@2026", hash));
        assertFalse(PasswordUtil.checkPassword(null, hash));
        assertFalse(PasswordUtil.checkPassword(plain, null));
    }

    @Test
    public void testEmailValidation() {
        assertTrue(ValidationUtil.isValidEmail("student@campus.edu"));
        assertTrue(ValidationUtil.isValidEmail("john.doe_12@sub.domain.org"));
        assertFalse(ValidationUtil.isValidEmail("invalid-email"));
        assertFalse(ValidationUtil.isValidEmail("@campus.edu"));
        assertFalse(ValidationUtil.isValidEmail("student@"));
        assertFalse(ValidationUtil.isValidEmail(null));
    }

    @Test
    public void testMobileValidation() {
        assertTrue(ValidationUtil.isValidMobile("9876543210"));
        assertTrue(ValidationUtil.isValidMobile("+919876543210"));
        assertFalse(ValidationUtil.isValidMobile("12345")); // too short
        assertFalse(ValidationUtil.isValidMobile("abcdefghij"));
        assertFalse(ValidationUtil.isValidMobile(null));
    }

    @Test
    public void testStudentIdValidation() {
        assertTrue(ValidationUtil.isValidId("STU1001"));
        assertTrue(ValidationUtil.isValidId("2024-CS-001"));
        assertTrue(ValidationUtil.isValidId("ALEX_2026"));
        assertFalse(ValidationUtil.isValidId("ab")); // too short
        assertFalse(ValidationUtil.isValidId("invalid id with spaces"));
        assertFalse(ValidationUtil.isValidId(null));
    }

    @Test
    public void testStudyYearValidation() {
        assertTrue(ValidationUtil.isValidStudyYear(1));
        assertTrue(ValidationUtil.isValidStudyYear(6));
        assertFalse(ValidationUtil.isValidStudyYear(0));
        assertFalse(ValidationUtil.isValidStudyYear(7));
    }

    @Test
    public void testPriorityAndStatusValidation() {
        assertTrue(ValidationUtil.isValidPriority("LOW"));
        assertTrue(ValidationUtil.isValidPriority("MEDIUM"));
        assertTrue(ValidationUtil.isValidPriority("HIGH"));
        assertFalse(ValidationUtil.isValidPriority("URGENT"));

        assertTrue(ValidationUtil.isValidStatus("PENDING"));
        assertTrue(ValidationUtil.isValidStatus("ASSIGNED"));
        assertTrue(ValidationUtil.isValidStatus("IN_PROGRESS"));
        assertTrue(ValidationUtil.isValidStatus("RESOLVED"));
        assertTrue(ValidationUtil.isValidStatus("REJECTED"));
        assertTrue(ValidationUtil.isValidStatus("WITHDRAWN"));
        assertFalse(ValidationUtil.isValidStatus("UNKNOWN"));
    }

    @Test
    public void testRemarksValidation() {
        assertTrue(ValidationUtil.isValidRemarks("Replaced damaged ethernet connector in lab."));
        assertFalse(ValidationUtil.isValidRemarks(null));
        assertFalse(ValidationUtil.isValidRemarks("   "));

        StringBuilder longRemarks = new StringBuilder();
        for (int i = 0; i < 505; i++) {
            longRemarks.append("A");
        }
        assertFalse(ValidationUtil.isValidRemarks(longRemarks.toString()));
    }
}
