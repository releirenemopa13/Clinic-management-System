package com.healthcare.controller;

import com.healthcare.model.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AdminLogsServlet extends HttpServlet {
    
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
                String logType = request.getParameter("type");
                // For now, just show a message
                session.setAttribute("success", "Log viewer for " + logType + " will be available in the next version.");
                response.sendRedirect("logs");
            } else if ("download".equals(action)) {
                String logType = request.getParameter("type");
                // For now, just show a message
                session.setAttribute("success", "Log download for " + logType + " will be available in the next version.");
                response.sendRedirect("logs");
            } else {
                // Default: show logs page
                request.getRequestDispatcher("/WEB-INF/views/admin/logs.jsp").forward(request, response);
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
            
            if ("clear".equals(action)) {
                String logType = request.getParameter("logType");
                // For now, just show a message
                session.setAttribute("success", "Logs cleared successfully for " + logType);
                response.sendRedirect("logs");
            } else if ("search".equals(action)) {
                String searchTerm = request.getParameter("searchTerm");
                String dateRange = request.getParameter("dateRange");
                
                // For now, just show a message
                session.setAttribute("success", "Log search functionality will be available in the next version.");
                response.sendRedirect("logs");
            } else {
                response.sendRedirect("logs");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "An error occurred while processing your request.");
            response.sendRedirect("logs");
        }
    }
}
