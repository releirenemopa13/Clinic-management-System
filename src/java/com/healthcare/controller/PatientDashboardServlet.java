package com.healthcare.controller;

import com.healthcare.model.User;
import com.healthcare.dao.VitalDAO;
import com.healthcare.dao.MedicationDAO;
import com.healthcare.dao.AppointmentDAO;
import com.healthcare.model.Vital;
import com.healthcare.model.Medication;
import com.healthcare.model.Appointment;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/patient/dashboard")
public class PatientDashboardServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        System.out.println("DEBUG: PatientDashboardServlet - Session exists: " + (session != null ? "YES" : "NO"));
        User user = (User) session.getAttribute("user");
        
        System.out.println("DEBUG: PatientDashboardServlet accessed - User: " + 
            (user != null ? user.getName() : "null") + ", Role: " + 
            (user != null ? user.getRole() : "null"));
        
        if (user != null) {
            System.out.println("DEBUG: User role from session: '" + user.getRole() + "'");
            System.out.println("DEBUG: Role comparison - Expected: 'PATIENT', Actual: '" + user.getRole() + "', Matches: " + "PATIENT".equalsIgnoreCase(user.getRole()));
        } else {
            System.out.println("DEBUG: User is null - no session data found");
        }
        
        if (user == null || !"PATIENT".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/unauthorized.jsp");
            return;
        }
        
        try {
            // Get patient ID from user
            // Note: In a real implementation, you would get patient ID from database
            int patientId = getPatientId(user.getId());
            
            VitalDAO vitalDAO = new VitalDAO();
            MedicationDAO medicationDAO = new MedicationDAO();
            AppointmentDAO appointmentDAO = new AppointmentDAO();
            
            // Get recent vitals (last 5)
            List<Vital> recentVitals = vitalDAO.getRecentVitals(patientId, 5);
            request.setAttribute("recentVitals", recentVitals);
            
            // Get upcoming appointments (next 3)
            List<Appointment> upcomingAppointments = appointmentDAO.getUpcomingAppointmentsByPatient(patientId, 3);
            request.setAttribute("upcomingAppointments", upcomingAppointments);
            
            // Get active medications count
            int activeMedicationsCount = medicationDAO.getActiveMedicationsCount(patientId);
            request.setAttribute("activeMedicationsCount", activeMedicationsCount);
            
            // Get medications due today
            List<Medication> medicationsDueToday = medicationDAO.getMedicationsDueToday(patientId);
            request.setAttribute("medicationsDueToday", medicationsDueToday);
            
            // Set user information for display
            request.setAttribute("userName", user.getName());
            request.setAttribute("userEmail", user.getEmail());
            
            // Forward to patient dashboard JSP
            request.getRequestDispatcher("/WEB-INF/views/patient/dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
    
    private int getPatientId(int userId) {
        // This is a placeholder - in real implementation, you would query the database
        // to get the patient ID associated with this user ID
        try {
            com.healthcare.dao.PatientDAO patientDAO = new com.healthcare.dao.PatientDAO();
            com.healthcare.model.Patient patient = patientDAO.getPatientByUserId(userId);
            return patient != null ? patient.getId() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
