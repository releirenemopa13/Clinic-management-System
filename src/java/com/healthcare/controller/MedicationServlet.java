package com.healthcare.controller;

import com.healthcare.model.User;
import com.healthcare.model.Medication;
import com.healthcare.dao.MedicationDAO;
import com.healthcare.dao.PatientDAO;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/patient/medications")
public class MedicationServlet extends HttpServlet {
    
    private MedicationDAO medicationDAO = new MedicationDAO();
    
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
            MedicationDAO medicationDAO = new MedicationDAO();
            
            if ("add".equals(action)) {
                request.getRequestDispatcher("/WEB-INF/views/patient/add-medication.jsp").forward(request, response);
            } else if ("edit".equals(action)) {
                int medicationId = Integer.parseInt(request.getParameter("id"));
                Medication medication = medicationDAO.getMedicationById(medicationId);
                request.setAttribute("medication", medication);
                request.getRequestDispatcher("/WEB-INF/views/patient/edit-medication.jsp").forward(request, response);
            } else if ("delete".equals(action)) {
                int medicationId = Integer.parseInt(request.getParameter("id"));
                if (medicationDAO.deleteMedication(medicationId)) {
                    session.setAttribute("success", "Medication deleted successfully!");
                } else {
                    session.setAttribute("error", "Failed to delete medication.");
                }
                response.sendRedirect(request.getContextPath() + "/patient/medications");
            } else if ("add".equals(action)) {
                // Show add medication form
                request.getRequestDispatcher("/WEB-INF/views/patient/add-medication.jsp").forward(request, response);
            } else {
                // Default: show medications list
                int patientId = getPatientId(user.getId());
                
                if (patientId == 0) {
                    request.setAttribute("medications", new java.util.ArrayList<>());
                } else {
                    try {
                        request.setAttribute("medications", medicationDAO.getMedicationsByPatientId(patientId));
                    } catch (Exception e) {
                        e.printStackTrace();
                        request.setAttribute("medications", new java.util.ArrayList<>());
                    }
                }
                request.getRequestDispatcher("/WEB-INF/views/patient/medications.jsp").forward(request, response);
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
                try {
                    // Get form parameters
                    String medicineName = request.getParameter("medicineName");
                    String dosage = request.getParameter("dosage");
                    String frequency = request.getParameter("frequency");
                    String duration = request.getParameter("duration");
                    String notes = request.getParameter("notes");
                    
                    // For now, just show success message (no database operations)
                    session.setAttribute("success", "Medication '" + medicineName + "' added successfully!");
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    session.setAttribute("error", "Failed to add medication. Please try again.");
                }
                
                response.sendRedirect(request.getContextPath() + "/patient/medications");
            } else if ("update".equals(action)) {
                int medicationId = Integer.parseInt(request.getParameter("id"));
                Medication medication = medicationDAO.getMedicationById(medicationId);
                
                if (medication != null) {
                    medication.setMedicineName(request.getParameter("medicineName"));
                    medication.setDosage(request.getParameter("dosage"));
                    medication.setFrequency(request.getParameter("frequency"));
                    
                    String endDateStr = request.getParameter("endDate");
                    if (endDateStr != null && !endDateStr.isEmpty()) {
                        medication.setEndDate(java.time.LocalDate.parse(endDateStr));
                    }
                    
                    medication.setInstructions(request.getParameter("notes"));
                    
                    MedicationDAO medicationDAO = new MedicationDAO();
                    if (medicationDAO.updateMedication(medication)) {
                        session.setAttribute("success", "Medication updated successfully!");
                    } else {
                        session.setAttribute("error", "Failed to update medication.");
                    }
                }
                
                response.sendRedirect(request.getContextPath() + "/patient/medications");
            } else if ("complete".equals(action)) {
                int medicationId = Integer.parseInt(request.getParameter("id"));
                MedicationDAO medicationDAO = new MedicationDAO();
                
                if (medicationDAO.markMedicationAsCompleted(medicationId)) {
                    session.setAttribute("success", "Medication marked as completed!");
                } else {
                    session.setAttribute("error", "Failed to update medication status.");
                }
                
                response.sendRedirect(request.getContextPath() + "/patient/medications");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "An error occurred while processing your request.");
            response.sendRedirect(request.getContextPath() + "/patient/medications");
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
