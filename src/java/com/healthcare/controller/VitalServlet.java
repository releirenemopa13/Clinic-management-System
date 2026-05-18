package com.healthcare.controller;

import com.healthcare.model.User;
import com.healthcare.model.Vital;
import com.healthcare.dao.VitalDAO;
import com.healthcare.dao.PatientDAO;
import java.io.IOException;
import java.time.LocalDateTime;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/patient/vitals")
public class VitalServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        
        if (user == null || !"PATIENT".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/unauthorized.jsp");
            return;
        }
        
        try {
            String action = request.getParameter("action"); 
            VitalDAO vitalDAO = new VitalDAO();
            
            if ("add".equals(action)) {
                request.getRequestDispatcher("/WEB-INF/views/patient/add-vital.jsp").forward(request, response);
            } else if ("history".equals(action)) {
                int patientId = getPatientId(user.getId());
                String days = request.getParameter("days");
                
                if (days != null) {
                    int daysInt = Integer.parseInt(days);
                    request.setAttribute("vitals", vitalDAO.getVitalsByDays(patientId, daysInt));
                } else {
                    request.setAttribute("vitals", vitalDAO.getVitalsByPatientId(patientId));
                }
                request.getRequestDispatcher("/WEB-INF/views/patient/vital-history.jsp").forward(request, response);
            } else {
                // Default: show recent vitals
                int patientId = getPatientId(user.getId());
                request.setAttribute("vitals", vitalDAO.getRecentVitals(patientId, 10));
                request.getRequestDispatcher("/WEB-INF/views/patient/vitals.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        
        if (user == null || !"PATIENT".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/unauthorized.jsp");
            return;
        }
        
        try {
            String action = request.getParameter("action");
            
            if ("save".equals(action)) {
                Vital vital = new Vital();
                vital.setPatientId(getPatientId(user.getId()));
                vital.setBpSystolic(Integer.parseInt(request.getParameter("bpSystolic")));
                vital.setBpDiastolic(Integer.parseInt(request.getParameter("bpDiastolic")));
                vital.setHeartRate(Integer.parseInt(request.getParameter("heartRate")));
                vital.setBloodSugar(Double.parseDouble(request.getParameter("bloodSugar")));
                vital.setWeight(Double.parseDouble(request.getParameter("weight")));
                vital.setTemperature(Double.parseDouble(request.getParameter("temperature")));
                vital.setRecordedAt(LocalDateTime.now());
                
                VitalDAO vitalDAO = new VitalDAO();
                if (vitalDAO.createVital(vital)) {
                    session.setAttribute("success", "Vital signs recorded successfully!");
                } else {
                    session.setAttribute("error", "Failed to record vital signs. Please try again.");
                }
                
                response.sendRedirect(request.getContextPath() + "/patient/vitals");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "An error occurred while saving vital signs.");
            response.sendRedirect(request.getContextPath() + "/patient/vitals");
        }
    }
    
    private int getPatientId(int userId) {
        try {
            PatientDAO patientDAO = new PatientDAO();
            com.healthcare.model.Patient patient = patientDAO.getPatientByUserId(userId);
            return patient != null ? patient.getId() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
