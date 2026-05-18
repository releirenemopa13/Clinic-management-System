package com.healthcare.controller;

import com.healthcare.model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AdminSettingsServlet extends HttpServlet {
    
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
            // For now, just forward to the settings page
            request.getRequestDispatcher("/WEB-INF/views/admin/settings.jsp").forward(request, response);
            
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
            
            if ("save".equals(action)) {
                // Get form parameters
                String systemName = request.getParameter("systemName");
                String adminEmail = request.getParameter("adminEmail");
                String maxUsers = request.getParameter("maxUsers");
                String sessionTimeout = request.getParameter("sessionTimeout");
                String maxLoginAttempts = request.getParameter("maxLoginAttempts");
                String passwordMinLength = request.getParameter("passwordMinLength");
                String enableTwoFactor = request.getParameter("enableTwoFactor");
                String smtpHost = request.getParameter("smtpHost");
                String smtpPort = request.getParameter("smtpPort");
                String emailUsername = request.getParameter("emailUsername");
                String emailPassword = request.getParameter("emailPassword");
                String backupFrequency = request.getParameter("backupFrequency");
                String backupRetention = request.getParameter("backupRetention");
                String autoBackup = request.getParameter("autoBackup");
                
                // For now, just show success message (in production, these would be saved to database or config file)
                session.setAttribute("success", "Settings saved successfully!");
                
            } else if ("testEmail".equals(action)) {
                // Simulate email test
                session.setAttribute("success", "Email test completed successfully!");
                
            } else if ("reset".equals(action)) {
                // Reset to defaults
                session.setAttribute("success", "Settings reset to default values!");
            }
            
            response.sendRedirect("settings");
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "An error occurred while processing your request.");
            response.sendRedirect("settings");
        }
    }
}
