package com.healthcare.controller;

import com.healthcare.model.User;
import com.healthcare.dao.UserDAO;
import com.healthcare.dao.AppointmentDAO;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AdminDashboardServlet extends HttpServlet {
    
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
            UserDAO userDAO = new UserDAO();
            AppointmentDAO appointmentDAO = new AppointmentDAO();
            
            // Get total users count
            int totalUsers = getTotalUsers();
            request.setAttribute("totalUsers", totalUsers);
            
            // Get today's appointments count
            int todayAppointments = getTodayAppointments();
            request.setAttribute("todayAppointments", todayAppointments);
            
            // Get active doctors count
            int activeDoctors = getActiveDoctors();
            request.setAttribute("activeDoctors", activeDoctors);
            
            // Get system health status
            boolean dbConnected = checkDatabaseConnection();
            boolean serverRunning = checkServerStatus();
            request.setAttribute("dbConnected", dbConnected);
            request.setAttribute("serverRunning", serverRunning);
            
            // Get recent activities (last 10 logins)
            List<User> recentUsers = getRecentUsers();
            request.setAttribute("recentUsers", recentUsers);
            
            // Set user information for display
            request.setAttribute("userName", user.getName());
            request.setAttribute("userEmail", user.getEmail());
            
            // Forward to admin dashboard JSP
            request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
    
    private int getTotalUsers() {
        try {
            UserDAO userDAO = new UserDAO();
            List<User> users = userDAO.getAllUsers();
            return users.size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    private int getTodayAppointments() {
        try {
            // This is a placeholder - in real implementation, you would query the database
            // to get count of appointments for today
            return 25; // Example count
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    private boolean checkDatabaseConnection() {
        try {
            com.healthcare.util.DatabaseUtil.getConnection().close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean checkServerStatus() {
        try {
            // This is a placeholder - in real implementation, you would check server status
            return true; // Assume server is running
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private int getActiveDoctors() {
        try {
            UserDAO userDAO = new UserDAO();
            List<User> doctors = userDAO.getUsersByRole("DOCTOR");
            return doctors.size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    private List<User> getRecentUsers() {
        try {
            // This is a placeholder - in real implementation, you would query the database
            // to get recent login activities from system_logs table
            UserDAO userDAO = new UserDAO();
            return userDAO.getAllUsers().subList(0, Math.min(10, userDAO.getAllUsers().size()));
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
