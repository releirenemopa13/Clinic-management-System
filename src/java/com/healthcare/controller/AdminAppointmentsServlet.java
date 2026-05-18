package com.healthcare.controller;

import com.healthcare.model.User;
import com.healthcare.dao.AppointmentDAO;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AdminAppointmentsServlet extends HttpServlet {
    
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    
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
                int appointmentId = Integer.parseInt(request.getParameter("id"));
                // For now, just show a message
                session.setAttribute("success", "Appointment details view will be available in the next version.");
                response.sendRedirect("appointments");
            } else if ("manage".equals(action)) {
                int appointmentId = Integer.parseInt(request.getParameter("id"));
                // For now, just show a message
                session.setAttribute("success", "Appointment management will be available in the next version.");
                response.sendRedirect("appointments");
            } else {
                // Default: show all appointments
                try {
                    List appointments = appointmentDAO.getAllAppointments();
                    request.setAttribute("appointments", appointments);
                } catch (Exception e) {
                    // If database query fails, continue without data
                    request.setAttribute("appointments", null);
                }
                request.getRequestDispatcher("/WEB-INF/views/admin/appointments.jsp").forward(request, response);
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
                String dateFilter = request.getParameter("dateFilter");
                
                // For now, just show a message
                session.setAttribute("success", "Appointment search functionality will be available in the next version.");
                response.sendRedirect("appointments");
            } else if ("export".equals(action)) {
                String exportType = request.getParameter("exportType");
                
                // For now, just show a message
                session.setAttribute("success", "Appointment export functionality will be available in the next version.");
                response.sendRedirect("appointments");
            } else {
                response.sendRedirect("appointments");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "An error occurred while processing your request.");
            response.sendRedirect("appointments");
        }
    }
}
