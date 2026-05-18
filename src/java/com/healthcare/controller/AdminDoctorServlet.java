package com.healthcare.controller;

import com.healthcare.dao.DoctorDAO;
import com.healthcare.model.Doctor;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/admin/doctors")
public class AdminDoctorServlet extends HttpServlet {
    
    private DoctorDAO doctorDAO = new DoctorDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        try {
            if ("add".equals(action)) {
                request.getRequestDispatcher("/WEB-INF/views/admin/addDoctor.jsp").forward(request, response);
            } else if ("edit".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Doctor doctor = doctorDAO.getDoctorById(id);
                request.setAttribute("doctor", doctor);
                request.getRequestDispatcher("/WEB-INF/views/admin/editDoctor.jsp").forward(request, response);
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                doctorDAO.deleteDoctor(id);
                response.sendRedirect(request.getContextPath() + "/admin/doctors");
            } else {
                List<Doctor> doctors = doctorDAO.getAllDoctors();
                request.setAttribute("doctors", doctors);
                request.getRequestDispatcher("/WEB-INF/views/admin/doctors.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/doctors.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid doctor ID");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        try {
            if ("add".equals(action)) {
                // For simplicity, show message; implement full add later
                request.setAttribute("error", "Adding new doctor requires user creation first. Please use 'Add User' with role DOCTOR.");
                request.getRequestDispatcher("/WEB-INF/views/admin/addDoctor.jsp").forward(request, response);
            } else if ("edit".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Doctor doctor = doctorDAO.getDoctorById(id);
                doctor.setSpecialization(request.getParameter("specialization"));
                doctor.setQualification(request.getParameter("qualification"));
                doctor.setExperience(Integer.parseInt(request.getParameter("experience")));
                // FIXED: parse as double, not BigDecimal
                doctor.setConsultationFee(Double.parseDouble(request.getParameter("consultationFee")));
                doctor.setAvailableDays(request.getParameter("availableDays"));
                doctor.setWorkingHours(request.getParameter("workingHours"));
                doctor.setActive("on".equals(request.getParameter("active")));
                doctorDAO.updateDoctor(doctor);
                response.sendRedirect(request.getContextPath() + "/admin/doctors");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/doctors.jsp").forward(request, response);
        }
    }
}