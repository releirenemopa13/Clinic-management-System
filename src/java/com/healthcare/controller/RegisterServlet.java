package com.healthcare.controller;

import com.healthcare.dao.UserDAO;
import com.healthcare.dao.PatientDAO;
import com.healthcare.model.User;
import com.healthcare.model.Patient;
import com.healthcare.service.EmailService;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String phone = request.getParameter("phone");
        String role = request.getParameter("role");
        
        // Normalize inputs
        if (email != null) email = email.trim().toLowerCase();
        if (phone != null) phone = phone.trim();
        if (name != null) name = name.trim();
        
        // Basic validations
        if (name == null || name.isEmpty() ||
            email == null || email.isEmpty() ||
            password == null || password.isEmpty() ||
            role == null || role.isEmpty()) {
            request.setAttribute("error", "All fields are required");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }
        
        if (password.length() < 6) {
            request.setAttribute("error", "Password must be at least 6 characters");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }
        
        // --- Patient fields validation (prevents 500) ---
        String dobStr = request.getParameter("dob");
        if (dobStr == null || dobStr.trim().isEmpty()) {
            request.setAttribute("error", "Date of birth is required");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }
        LocalDate dob;
        try {
            dob = LocalDate.parse(dobStr);
        } catch (DateTimeParseException e) {
            request.setAttribute("error", "Invalid date format (use YYYY-MM-DD)");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }
        
        String bloodGroup = request.getParameter("bloodGroup");
        String allergies = request.getParameter("allergies");
        String emergencyContact = request.getParameter("emergencyContact");
        String address = request.getParameter("address");
        
        // Trim long fields to avoid DB truncation
        if (bloodGroup != null && bloodGroup.length() > 10) bloodGroup = bloodGroup.substring(0, 10);
        if (emergencyContact != null && emergencyContact.length() > 20) emergencyContact = emergencyContact.substring(0, 20);
        if (phone != null && phone.length() > 20) phone = phone.substring(0, 20);
        
        // --- Registration with rollback ---
        UserDAO userDAO = new UserDAO();
        PatientDAO patientDAO = new PatientDAO();
        int userId = -1;
        
        try {
            // 1. Create user
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password); // TODO: hash!
            user.setPhone(phone);
            user.setRole(role);
            user.setActive(true);
            
            userId = userDAO.createUser(user);
            if (userId <= 0) throw new Exception("User creation failed");
            
            // 2. Create patient
            Patient patient = new Patient();
            patient.setUserId(userId);
            patient.setDateOfBirth(dob);
            patient.setBloodGroup(bloodGroup);
            patient.setAllergies(allergies);
            patient.setEmergencyContact(emergencyContact);
            patient.setAddress(address);
            
            boolean patientCreated = patientDAO.createPatient(patient);
            if (!patientCreated) throw new Exception("Patient creation failed");
            
            // 3. Send email (best effort – does NOT break registration)
            try {
                EmailService emailService = new EmailService();
                boolean emailSent = emailService.sendRegistrationConfirmation(email, name);
                if (emailSent) {
                    System.out.println("📧 Registration email sent to: " + email);
                } else {
                    System.err.println("⚠️ Failed to send email to: " + email);
                }
            } catch (Exception e) {
                System.err.println("❌ Email error for " + email + ": " + e.getMessage());
                e.printStackTrace();
            }
            
            // 4. Success: store message and redirect to login
            HttpSession session = request.getSession();
            session.setAttribute("success", "Registration successful! Please login.");
            response.sendRedirect("login");
            
        } catch (Exception e) {
            e.printStackTrace();
            
            // Rollback: delete user if it was created
            if (userId > 0) {
                try {
                    userDAO.deleteUser(userId);
                    System.out.println("Rolled back user ID " + userId);
                } catch (Exception ex) {
                    System.err.println("Rollback failed: " + ex.getMessage());
                }
            }
            
            // Show user-friendly error
            String msg = e.getMessage();
            if (msg != null && (msg.contains("Duplicate") || msg.contains("UNIQUE"))) {
                request.setAttribute("error", "Email already registered");
            } else if (msg != null && msg.contains("Data too long")) {
                request.setAttribute("error", "One or more fields exceed maximum length");
            } else {
                request.setAttribute("error", "Registration failed: " + msg);
            }
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }
}