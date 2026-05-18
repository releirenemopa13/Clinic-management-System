package com.healthcare.controller;

import com.healthcare.model.User;
import com.healthcare.dao.AppointmentDAO;
import com.healthcare.dao.PatientDAO;
import com.healthcare.dao.DoctorDAO;
import com.healthcare.model.Appointment;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet({"/patient/appointments", "/doctor/appointments"})
public class AppointmentServlet extends HttpServlet {
    
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
            
            if ("book".equals(action) && "PATIENT".equals(user.getRole())) {
                // Simple booking page
                request.getRequestDispatcher("/WEB-INF/views/patient/book-appointment.jsp").forward(request, response);
            } else if ("view".equals(action) && "DOCTOR".equals(user.getRole())) {
                // View appointment details
                request.getRequestDispatcher("/WEB-INF/views/doctor/appointment-details.jsp").forward(request, response);
            } else if ("confirm".equals(action) && "DOCTOR".equals(user.getRole())) {
                // Confirm appointment
                try {
                    String appointmentId = request.getParameter("id");
                    // For now, just show success message
                    session.setAttribute("success", "Appointment #" + appointmentId + " confirmed successfully!");
                } catch (Exception e) {
                    e.printStackTrace();
                    session.setAttribute("error", "Failed to confirm appointment. Please try again.");
                }
                response.sendRedirect(request.getContextPath() + "/doctor/appointments");
            } else if ("cancel".equals(action) && "DOCTOR".equals(user.getRole())) {
                // Cancel appointment
                try {
                    String appointmentId = request.getParameter("id");
                    // For now, just show success message
                    session.setAttribute("success", "Appointment #" + appointmentId + " cancelled successfully!");
                } catch (Exception e) {
                    e.printStackTrace();
                    session.setAttribute("error", "Failed to cancel appointment. Please try again.");
                }
                response.sendRedirect(request.getContextPath() + "/doctor/appointments");
            } else if ("complete".equals(action) && "DOCTOR".equals(user.getRole())) {
                // Complete appointment
                try {
                    String appointmentId = request.getParameter("id");
                    // For now, just show success message
                    session.setAttribute("success", "Appointment #" + appointmentId + " marked as completed!");
                } catch (Exception e) {
                    e.printStackTrace();
                    session.setAttribute("error", "Failed to complete appointment. Please try again.");
                }
                response.sendRedirect(request.getContextPath() + "/doctor/appointments");
            } else if ("reschedule".equals(action) && "DOCTOR".equals(user.getRole())) {
                // Reschedule appointment
                try {
                    String appointmentId = request.getParameter("id");
                    // For now, just show success message
                    session.setAttribute("success", "Appointment #" + appointmentId + " rescheduled successfully!");
                } catch (Exception e) {
                    e.printStackTrace();
                    session.setAttribute("error", "Failed to reschedule appointment. Please try again.");
                }
                response.sendRedirect(request.getContextPath() + "/doctor/appointments");
            } else if ("book".equals(action) && "DOCTOR".equals(user.getRole())) {
                // Book follow-up appointment
                try {
                    String patientId = request.getParameter("patientId");
                    // For now, just show success message
                    session.setAttribute("success", "Follow-up appointment booking for patient #" + patientId + " would be displayed here.");
                } catch (Exception e) {
                    e.printStackTrace();
                    session.setAttribute("error", "Failed to open follow-up booking. Please try again.");
                }
                response.sendRedirect(request.getContextPath() + "/doctor/appointments");
            } else if ("PATIENT".equals(user.getRole())) {
                // Default: show appointments
                request.getRequestDispatcher("/WEB-INF/views/patient/appointments.jsp").forward(request, response);
            } else if ("DOCTOR".equals(user.getRole())) {
                // Doctor appointments
                request.getRequestDispatcher("/WEB-INF/views/doctor/appointments.jsp").forward(request, response);
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
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/unauthorized.jsp");
            return;
        }
        
        try {
            String action = request.getParameter("action");
            
            if ("save".equals(action) && "PATIENT".equals(user.getRole())) {
                try {
                    // Get form parameters
                    String doctor = request.getParameter("doctor");
                    String date = request.getParameter("date");
                    String time = request.getParameter("time");
                    String symptoms = request.getParameter("symptoms");
                    
                    // For now, just show success message (no database operations)
                    session.setAttribute("success", "Appointment booked successfully for " + date + " at " + time + "!");
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    session.setAttribute("error", "Failed to book appointment. Please try again.");
                }
                
                response.sendRedirect(request.getContextPath() + "/patient/appointments");
            } else if ("update".equals(action) && ("DOCTOR".equals(user.getRole()) || "ADMIN".equals(user.getRole()))) {
                int appointmentId = Integer.parseInt(request.getParameter("id"));
                String status = request.getParameter("status");
                String notes = request.getParameter("consultationNotes");
                
                AppointmentDAO appointmentDAO = new AppointmentDAO();
                Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
                
                if (appointment != null) {
                    appointment.setStatus(status);
                    if (notes != null) {
                        appointment.setConsultationNotes(notes);
                    }
                    
                    if (appointmentDAO.updateAppointment(appointment)) {
                        session.setAttribute("success", "Appointment updated successfully!");
                    } else {
                        session.setAttribute("error", "Failed to update appointment.");
                    }
                }
                
                response.sendRedirect("appointments?action=manage");
            } else if ("cancel".equals(action)) {
                int appointmentId = Integer.parseInt(request.getParameter("id"));
                AppointmentDAO appointmentDAO = new AppointmentDAO();
                
                if (appointmentDAO.cancelAppointment(appointmentId)) {
                    session.setAttribute("success", "Appointment cancelled successfully!");
                } else {
                    session.setAttribute("error", "Failed to cancel appointment.");
                }
                
                response.sendRedirect(request.getContextPath() + "/patient/appointments");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "An error occurred while processing your request.");
            response.sendRedirect(request.getContextPath() + "/patient/appointments");
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
}
