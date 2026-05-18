package com.healthcare.controller;

import com.healthcare.model.User;
import com.healthcare.dao.UserDAO;
import com.healthcare.dao.PatientDAO;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AdminPatientsServlet extends HttpServlet {
    
    private UserDAO userDAO = new UserDAO();
    private PatientDAO patientDAO = new PatientDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Set cache control headers to prevent back button
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        
        if (user == null || !"ADMIN".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/unauthorized.jsp");
            return;
        }
        
        try {
            String action = request.getParameter("action");
            
            if ("view".equals(action)) {
                int patientId = Integer.parseInt(request.getParameter("id"));
                // For now, redirect to users edit since patient profile is linked to user
                response.sendRedirect("users?action=edit&id=" + patientId);
            } else if ("details".equals(action)) {
                int patientId = Integer.parseInt(request.getParameter("id"));
                // For now, just show a message
                session.setAttribute("success", "Patient details view will be available in the next version.");
                response.sendRedirect("patients");
            } else {
                // Default: show all patients
                try {
                    List<User> patients = userDAO.getUsersByRole("PATIENT");
                    request.setAttribute("patients", patients);
                } catch (Exception e) {
                    // If database query fails, continue without data
                    request.setAttribute("patients", null);
                }
                request.getRequestDispatcher("/WEB-INF/views/admin/patients.jsp").forward(request, response);
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
        
        if (user == null || !"ADMIN".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/unauthorized.jsp");
            return;
        }
        
        try {
            String action = request.getParameter("action");
            
            if ("search".equals(action)) {
                String searchTerm = request.getParameter("search");
                try {
                    List<User> patients = userDAO.searchUsers(searchTerm);
                    // Filter to show only patients
                    patients.removeIf(u -> !"PATIENT".equals(u.getRole()));
                    request.setAttribute("patients", patients);
                    request.setAttribute("searchTerm", searchTerm);
                } catch (Exception e) {
                    request.setAttribute("patients", null);
                }
                request.getRequestDispatcher("/WEB-INF/views/admin/patients.jsp").forward(request, response);
            } else {
                response.sendRedirect("patients");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "An error occurred while processing your request.");
            response.sendRedirect("patients");
        }
    }
}
