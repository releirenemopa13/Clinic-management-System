package com.healthcare.util;

public class EmailUtil {
    
    // Email utility class - currently disabled for compilation
    // To enable email functionality, add JavaMail dependency to project
    
    public static boolean sendEmail(String to, String subject, String content) {
        System.out.println("Email would be sent to: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Content: " + content);
        return true;
    }
    
    public static boolean sendPasswordResetEmail(String to, String newPassword) {
        String subject = "HealthCare Plus - Password Reset";
        String content = "Hello,\n\n" +
                "Your password has been reset successfully.\n\n" +
                "New Password: " + newPassword + "\n\n" +
                "Please login with your new password and change it immediately.\n\n" +
                "If you did not request this password reset, please contact support.\n\n" +
                "Best regards,\n" +
                "HealthCare Plus Team";
        
        return sendEmail(to, subject, content);
    }
    
    public static boolean sendAppointmentConfirmationEmail(String to, String patientName, String doctorName, String date, String time) {
        String subject = "HealthCare Plus - Appointment Confirmation";
        String content = "Hello " + patientName + ",\n\n" +
                "Your appointment has been confirmed.\n\n" +
                "Doctor: " + doctorName + "\n" +
                "Date: " + date + "\n" +
                "Time: " + time + "\n\n" +
                "Please arrive 15 minutes before your scheduled time.\n\n" +
                "Best regards,\n" +
                "HealthCare Plus Team";
        
        return sendEmail(to, subject, content);
    }
    
    public static boolean sendRegistrationEmail(String to, String userName, String role) {
        String subject = "Welcome to HealthCare Plus";
        String content = "Hello " + userName + ",\n\n" +
                "Welcome to HealthCare Plus! Your " + role.toLowerCase() + " account has been created successfully.\n\n" +
                "You can now login to our system and start managing your healthcare needs.\n\n" +
                "Login URL: http://localhost:8080/ClinicManagementSystem/login\n\n" +
                "If you have any questions, please don't hesitate to contact our support team.\n\n" +
                "Best regards,\n" +
                "HealthCare Plus Team";
        
        return sendEmail(to, subject, content);
    }
    
    public static boolean sendPrescriptionEmail(String to, String patientName, String doctorName, String diagnosis) {
        String subject = "HealthCare Plus - New Prescription";
        String content = "Hello " + patientName + ",\n\n" +
                "Dr. " + doctorName + " has issued a new prescription for you.\n\n" +
                "Diagnosis: " + diagnosis + "\n\n" +
                "Please login to your account to view the complete prescription details.\n\n" +
                "Best regards,\n" +
                "HealthCare Plus Team";
        
        return sendEmail(to, subject, content);
    }
    
    public static boolean sendAppointmentReminderEmail(String to, String patientName, String doctorName, String date, String time) {
        String subject = "HealthCare Plus - Appointment Reminder";
        String content = "Hello " + patientName + ",\n\n" +
                "This is a reminder about your upcoming appointment.\n\n" +
                "Doctor: " + doctorName + "\n" +
                "Date: " + date + "\n" +
                "Time: " + time + "\n\n" +
                "Please arrive 15 minutes before your scheduled time.\n\n" +
                "Best regards,\n" +
                "HealthCare Plus Team";
        
        return sendEmail(to, subject, content);
    }
    
    public static boolean sendSystemNotificationEmail(String to, String message) {
        String subject = "HealthCare Plus - System Notification";
        String content = "Hello,\n\n" +
                message + "\n\n" +
                "Best regards,\n" +
                "HealthCare Plus Team";
        
        return sendEmail(to, subject, content);
    }
}
