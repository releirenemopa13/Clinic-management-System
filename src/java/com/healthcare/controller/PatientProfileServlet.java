package com.healthcare.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.healthcare.dao.PatientDAO;
import com.healthcare.dao.UserDAO;
import com.healthcare.model.Patient;
import com.healthcare.model.User;

@WebServlet("/patient/profile")
public class PatientProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Set cache control headers
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"PATIENT".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/unauthorized.jsp");
            return;
        }

        try {
            // Get patient information
            PatientDAO patientDAO = new PatientDAO();
            Patient patient = patientDAO.getPatientByUserId(user.getId());
            
            if (patient == null) {
                // Create default patient record if it doesn't exist
                patient = createDefaultPatientRecord(user.getId());
            }
            
            // Set request attributes
            request.setAttribute("patient", patient);
            request.setAttribute("user", user);
            request.setAttribute("userName", user.getName());
            request.setAttribute("userEmail", user.getEmail());
            
            // Forward to profile page
            request.getRequestDispatcher("/WEB-INF/views/patient/profile.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"PATIENT".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/unauthorized.jsp");
            return;
        }

        try {
            // Get form parameters
            String name = request.getParameter("name");
            String phone = request.getParameter("phone");
            String bloodGroup = request.getParameter("bloodGroup");
            String allergies = request.getParameter("allergies");
            String emergencyContact = request.getParameter("emergencyContact");
            String address = request.getParameter("address");
            
            // Update user information
            UserDAO userDAO = new UserDAO();
            user.setName(name);
            user.setPhone(phone);
            userDAO.updateUser(user);
            
            // Get patient information
            PatientDAO patientDAO = new PatientDAO();
            Patient patient = patientDAO.getPatientByUserId(user.getId());
            
            if (patient == null) {
                // Create default patient record if it doesn't exist
                patient = createDefaultPatientRecord(user.getId());
            }
            
            // Update patient profile
            patient.setBloodGroup(bloodGroup);
            patient.setAllergies(allergies);
            patient.setEmergencyContact(emergencyContact);
            patient.setAddress(address);
            
            // Update patient record
            if (patientDAO.updatePatient(patient)) {
                session.setAttribute("success", "Profile updated successfully!");
                // Update session user
                session.setAttribute("user", user);
                session.setAttribute("userName", user.getName());
            } else {
                session.setAttribute("error", "Failed to update profile");
            }
            
            response.sendRedirect(request.getContextPath() + "/patient/profile");
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "An error occurred while updating profile");
            response.sendRedirect(request.getContextPath() + "/patient/profile");
        }
    }
    
    private Patient createDefaultPatientRecord(int userId) {
        try {
            PatientDAO patientDAO = new PatientDAO();
            Patient patient = new Patient();
            
            // Set default values for the patient record
            patient.setUserId(userId);
            patient.setBloodGroup("O+");
            patient.setAllergies("None");
            patient.setEmergencyContact("0000000000");
            patient.setAddress("Not provided");
            
            // Create the patient record
            if (patientDAO.createPatient(patient)) {
                // Get the newly created patient
                return patientDAO.getPatientByUserId(userId);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
