package com.healthcare.controller;

import com.healthcare.model.User;
import com.healthcare.dao.PrescriptionDAO;
import com.healthcare.dao.PatientDAO;
import com.healthcare.dao.DoctorDAO;
import com.healthcare.model.Prescription;
import com.healthcare.model.PrescriptionMedicine;
import com.healthcare.dao.PrescriptionMedicineDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet({"/patient/prescriptions", "/doctor/prescriptions"})
public class PrescriptionServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/unauthorized.jsp");
            return;
        }
        
        try {
            String action = request.getParameter("action");
            
            if ("view".equals(action)) {
                // Simple view page
                request.getRequestDispatcher("/WEB-INF/views/prescription-details.jsp").forward(request, response);
            } else if ("PATIENT".equals(user.getRole())) {
                // Default: show prescriptions
                request.getRequestDispatcher("/WEB-INF/views/patient/prescriptions.jsp").forward(request, response);
            } else if ("DOCTOR".equals(user.getRole())) {
                // Doctor prescriptions
                request.getRequestDispatcher("/WEB-INF/views/doctor/prescriptions.jsp").forward(request, response);
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
        
        if (user == null || !"DOCTOR".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/unauthorized.jsp");
            return;
        }
        
        try {
            String action = request.getParameter("action");
            
            if ("save".equals(action)) {
                Prescription prescription = new Prescription();
                prescription.setAppointmentId(Integer.parseInt(request.getParameter("appointmentId")));
                prescription.setDoctorId(getDoctorId(user.getId()));
                prescription.setPatientId(Integer.parseInt(request.getParameter("patientId")));
                prescription.setDiagnosis(request.getParameter("diagnosis"));
                prescription.setNotes(request.getParameter("notes"));
                
                PrescriptionDAO prescriptionDAO = new PrescriptionDAO();
                PrescriptionMedicineDAO medicineDAO = new PrescriptionMedicineDAO();
                
                if (prescriptionDAO.createPrescription(prescription)) {
                    // Get the generated prescription ID
                    int prescriptionId = getLastPrescriptionId(prescription.getAppointmentId());
                    
                    // Add medicines
                    String[] medicineNames = request.getParameterValues("medicineName");
                    String[] dosages = request.getParameterValues("dosage");
                    String[] durations = request.getParameterValues("duration");
                    String[] instructions = request.getParameterValues("instructions");
                    
                    if (medicineNames != null) {
                        for (int i = 0; i < medicineNames.length; i++) {
                            PrescriptionMedicine medicine = new PrescriptionMedicine();
                            medicine.setPrescriptionId(prescriptionId);
                            medicine.setMedicineName(medicineNames[i]);
                            medicine.setDosage(dosages[i]);
                            medicine.setDuration(durations[i]);
                            medicine.setInstructions(instructions[i]);
                            medicineDAO.createPrescriptionMedicine(medicine);
                        }
                    }
                    
                    session.setAttribute("success", "Prescription created successfully!");
                } else {
                    session.setAttribute("error", "Failed to create prescription. Please try again.");
                }
                
                response.sendRedirect("prescriptions");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "An error occurred while creating prescription.");
            response.sendRedirect("prescriptions");
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
    
    private int createDefaultPatientRecord(int userId) {
        try {
            PatientDAO patientDAO = new PatientDAO();
            com.healthcare.model.Patient patient = new com.healthcare.model.Patient();
            
            // Set default values for the patient record
            patient.setUserId(userId);
            patient.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
            patient.setBloodGroup("O+");
            patient.setAllergies("None");
            patient.setEmergencyContact("0000000000");
            patient.setAddress("Not provided");
            
            // Create the patient record
            if (patientDAO.createPatient(patient)) {
                // Get the newly created patient
                com.healthcare.model.Patient createdPatient = patientDAO.getPatientByUserId(userId);
                return createdPatient != null ? createdPatient.getId() : 0;
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    private int getDoctorId(int userId) {
        try {
            DoctorDAO doctorDAO = new DoctorDAO();
            com.healthcare.model.Doctor doctor = doctorDAO.getDoctorByUserId(userId);
            return doctor != null ? doctor.getId() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    private int getLastPrescriptionId(int appointmentId) {
        try {
            PrescriptionDAO prescriptionDAO = new PrescriptionDAO();
            Prescription prescription = prescriptionDAO.getPrescriptionByAppointmentId(appointmentId);
            return prescription != null ? prescription.getId() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
