package com.healthcare.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtil {
    
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    public static final DateTimeFormatter DISPLAY_TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    
    // Format current date
    public static String getCurrentDate() {
        return LocalDate.now().format(DATE_FORMATTER);
    }
    
    // Format current datetime
    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DATETIME_FORMATTER);
    }
    
    // Format date for display
    public static String formatDateForDisplay(LocalDate date) {
        return date.format(DISPLAY_DATE_FORMATTER);
    }
    
    // Format datetime for display
    public static String formatDateTimeForDisplay(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_DATE_FORMATTER) + " at " + dateTime.format(DISPLAY_TIME_FORMATTER);
    }
    
    // Parse date string
    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }
    
    // Parse datetime string
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
    }
    
    // Get days between dates
    public static long getDaysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }
    
    // Check if date is today
    public static boolean isToday(LocalDate date) {
        return date.equals(LocalDate.now());
    }
    
    // Check if date is in the past
    public static boolean isPast(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }
    
    // Check if date is in the future
    public static boolean isFuture(LocalDate date) {
        return date.isAfter(LocalDate.now());
    }
    
    // Get age from date of birth
    public static int getAge(LocalDate dateOfBirth) {
        return (int) ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now());
    }
    
    // Add days to date
    public static LocalDate addDays(LocalDate date, int days) {
        return date.plusDays(days);
    }
    
    // Subtract days from date
    public static LocalDate subtractDays(LocalDate date, int days) {
        return date.minusDays(days);
    }
    
    // Get start of week
    public static LocalDate getStartOfWeek(LocalDate date) {
        return date.minusDays(date.getDayOfWeek().getValue() - 1);
    }
    
    // Get end of week
    public static LocalDate getEndOfWeek(LocalDate date) {
        return date.plusDays(7 - date.getDayOfWeek().getValue());
    }
    
    // Get start of month
    public static LocalDate getStartOfMonth(LocalDate date) {
        return date.withDayOfMonth(1);
    }
    
    // Get end of month
    public static LocalDate getEndOfMonth(LocalDate date) {
        return date.withDayOfMonth(date.lengthOfMonth());
    }
    
    // Format date range
    public static String formatDateRange(LocalDate startDate, LocalDate endDate) {
        return formatDateForDisplay(startDate) + " - " + formatDateForDisplay(endDate);
    }
    
    // Get relative time string
    public static String getRelativeTime(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        
        if (minutes < 1) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " minutes ago";
        } else if (minutes < 1440) { // 24 hours
            long hours = minutes / 60;
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else if (minutes < 10080) { // 7 days
            long days = minutes / 1440;
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        } else {
            long weeks = minutes / 10080;
            return weeks + " week" + (weeks > 1 ? "s" : "") + " ago";
        }
    }
    
    // Check if date is within range
    public static boolean isWithinRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
    
    // Get next occurrence of a day of week
    public static LocalDate getNextDayOfWeek(LocalDate date, java.time.DayOfWeek dayOfWeek) {
        LocalDate nextDate = date.plusDays(1);
        while (nextDate.getDayOfWeek() != dayOfWeek) {
            nextDate = nextDate.plusDays(1);
        }
        return nextDate;
    }
    
    // Format date for SQL
    public static String formatDateForSQL(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
    
    // Format datetime for SQL
    public static String formatDateTimeForSQL(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMATTER);
    }
}
