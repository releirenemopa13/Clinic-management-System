package com.healthcare.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.healthcare.model.User;

@WebServlet("/doctor/reports")
public class DoctorReportsServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"DOCTOR".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/unauthorized.jsp");
            return;
        }

        try {
            String action = request.getParameter("action");
            
            if ("upload".equals(action)) {
                // Upload reports
                String patientId = request.getParameter("patientId");
                HttpSession httpSession = request.getSession();
                httpSession.setAttribute("success", "Report upload interface for patient #" + patientId + " would be displayed here.");
                response.sendRedirect(request.getContextPath() + "/doctor/patients");
            } else {
                // Set request attributes
                request.setAttribute("userName", user.getName());
                request.setAttribute("userEmail", user.getEmail());
                
                // For now, just redirect to patients page
                response.sendRedirect(request.getContextPath() + "/doctor/patients");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}
