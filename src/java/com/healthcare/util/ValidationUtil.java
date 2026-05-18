package com.healthcare.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class ValidationUtil {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9]{10,15}$"
    );
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Validate email
    public static boolean isValidEmail(String email) {
        return email != null && !email.trim().isEmpty() && EMAIL_PATTERN.matcher(email).matches();
    }
    
    // Validate phone number
    public static boolean isValidPhone(String phone) {
        return phone != null && !phone.trim().isEmpty() && PHONE_PATTERN.matcher(phone).matches();
    }
    
    // Validate date
    public static boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }
        
        try {
            LocalDate.parse(dateStr, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    // Validate required field
    public static boolean isRequired(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    // Validate string length
    public static boolean isValidLength(String value, int minLength, int maxLength) {
        if (value == null) {
            return false;
        }
        int length = value.trim().length();
        return length >= minLength && length <= maxLength;
    }
    
    // Validate numeric value
    public static boolean isValidNumeric(String value, double min, double max) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        
        try {
            double num = Double.parseDouble(value.trim());
            return num >= min && num <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    // Validate blood pressure
    public static boolean isValidBloodPressure(String systolic, String diastolic) {
        try {
            int sys = Integer.parseInt(systolic);
            int dia = Integer.parseInt(diastolic);
            return sys >= 60 && sys <= 250 && dia >= 40 && dia <= 150 && sys > dia;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    // Validate heart rate
    public static boolean isValidHeartRate(String heartRate) {
        try {
            int hr = Integer.parseInt(heartRate);
            return hr >= 30 && hr <= 250;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    // Validate blood sugar
    public static boolean isValidBloodSugar(String bloodSugar) {
        try {
            double bs = Double.parseDouble(bloodSugar);
            return bs >= 20 && bs <= 600;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    // Validate weight
    public static boolean isValidWeight(String weight) {
        try {
            double w = Double.parseDouble(weight);
            return w >= 1 && w <= 500;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    // Validate temperature
    public static boolean isValidTemperature(String temperature) {
        try {
            double temp = Double.parseDouble(temperature);
            return temp >= 25 && temp <= 45;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    // Validate future date
    public static boolean isFutureDate(String dateStr) {
        if (!isValidDate(dateStr)) {
            return false;
        }
        
        try {
            LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
            LocalDate today = LocalDate.now();
            return date.isAfter(today);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    // Validate blood group
    public static boolean isValidBloodGroup(String bloodGroup) {
        if (bloodGroup == null) {
            return false;
        }
        
        String[] validGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String group : validGroups) {
            if (group.equals(bloodGroup)) {
                return true;
            }
        }
        return false;
    }
    
    // Sanitize string input
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        
        return input.trim()
                   .replaceAll("<", "&lt;")
                   .replaceAll(">", "&gt;")
                   .replaceAll("\"", "&quot;")
                   .replaceAll("'", "&#39;")
                   .replaceAll("&", "&amp;");
    }
    
    // Get validation error message
    public static String getErrorMessage(String field, String error) {
        return field + " " + error;
    }
    
    // Check if all required fields are present
    public static boolean validateRequired(String... fields) {
        for (String field : fields) {
            if (!isRequired(field)) {
                return false;
            }
        }
        return true;
    }
}
