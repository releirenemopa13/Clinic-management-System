package com.healthcare.service;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService {

    // --- CONFIGURATION (Replace with your details) ---
    private static final String FROM_EMAIL = "relebohile.mopa@bothouniversity.com"; // Your Gmail address
    private static final String APP_PASSWORD = "zydlbgcoobjucngv"; // The App Password you generated
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    // ----------------------------------------------

    public boolean sendRegistrationConfirmation(String toEmail, String patientName) {
        // 1. Set the mail server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        // 2. Create a session with an authenticator
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        // (Optional) Uncomment to see detailed SMTP logs
         session.setDebug(true);

        try {
            // 3. Create the email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("🎉 Welcome to HealthCare Plus!");
            
            // You can create an HTML email for a better look
            String emailContent = "<html>" +
                    "<body style='font-family: Arial, sans-serif;'>" +
                    "<h2 style='color: #2c3e50;'>Welcome to HealthCare Plus, " + patientName + "!</h2>" +
                    "<p>Your account has been successfully created.</p>" +
                    "<p>You can now log in to your dashboard to manage your health records, book appointments, and more.</p>" +
                    "<br>" +
                    "<p>Best regards,<br>The HealthCare Plus Team</p>" +
                    "</body>" +
                    "</html>";
            
            message.setContent(emailContent, "text/html; charset=utf-8");
            Transport.send(message);
            System.out.println("✅ Email sent to " + toEmail);
            return true;
        } catch (MessagingException e) {
            System.err.println("❌ Email failed to " + toEmail);
            e.printStackTrace();
            return false;
        }
    }
}