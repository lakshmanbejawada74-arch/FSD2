package com.campuscare.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Centralized server-side validation rules for inputs, credentials, and complaints.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$"
    );

    private static final Pattern MOBILE_PATTERN = Pattern.compile(
            "^\\+?[0-9]{10,15}$"
    );

    private static final Pattern ID_PATTERN = Pattern.compile(
            "^[A-Za-z0-9_-]{3,30}$"
    );

    private static final Set<String> ALLOWED_PRIORITIES = new HashSet<>(
            Arrays.asList("LOW", "MEDIUM", "HIGH")
    );

    private static final Set<String> ALLOWED_STATUSES = new HashSet<>(
            Arrays.asList("PENDING", "ASSIGNED", "IN_PROGRESS", "RESOLVED", "REJECTED", "WITHDRAWN")
    );

    private static final Set<String> ALLOWED_STUDENT_TYPES = new HashSet<>(
            Arrays.asList("HOSTEL", "DAY_SCHOLAR")
    );

    public static boolean isValidId(String id) {
        return id != null && ID_PATTERN.matcher(id.trim()).matches();
    }

    public static boolean isValidName(String name) {
        if (name == null) return false;
        String trimmed = name.trim();
        return trimmed.length() >= 2 && trimmed.length() <= 100;
    }

    public static boolean isValidDepartment(String dept) {
        if (dept == null) return false;
        String trimmed = dept.trim();
        return !trimmed.isEmpty() && trimmed.length() <= 80;
    }

    public static boolean isValidStudyYear(int year) {
        return year >= 1 && year <= 6;
    }

    public static boolean isValidStudentType(String type) {
        return type != null && ALLOWED_STUDENT_TYPES.contains(type.trim().toUpperCase());
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        String trimmed = email.trim();
        return trimmed.length() <= 150 && EMAIL_PATTERN.matcher(trimmed).matches();
    }

    public static boolean isValidMobile(String mobile) {
        if (mobile == null) return false;
        String trimmed = mobile.trim();
        return MOBILE_PATTERN.matcher(trimmed).matches();
    }

    public static boolean isValidComplaintTitle(String title) {
        if (title == null) return false;
        String trimmed = title.trim();
        return trimmed.length() >= 5 && trimmed.length() <= 100;
    }

    public static boolean isValidComplaintDescription(String desc) {
        if (desc == null) return false;
        String trimmed = desc.trim();
        return trimmed.length() >= 10 && trimmed.length() <= 1000;
    }

    public static boolean isValidPriority(String priority) {
        return priority != null && ALLOWED_PRIORITIES.contains(priority.trim().toUpperCase());
    }

    public static boolean isValidStatus(String status) {
        return status != null && ALLOWED_STATUSES.contains(status.trim().toUpperCase());
    }

    public static boolean isValidRemarks(String remarks) {
        if (remarks == null) return false;
        String trimmed = remarks.trim();
        return !trimmed.isEmpty() && trimmed.length() <= 500;
    }
}
