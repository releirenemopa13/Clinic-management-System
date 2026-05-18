package com.healthcare.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.healthcare.model.User;

@WebServlet("/doctor/patients")
public class DoctorPatientsServlet extends HttpServlet {
    
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
            
            if ("records".equals(action)) {
                // View medical records
                request.setAttribute("patientId", request.getParameter("id"));
                request.setAttribute("userName", user.getName());
                request.setAttribute("userEmail", user.getEmail());
                
                // Forward to medical records page
                request.getRequestDispatcher("/WEB-INF/views/doctor/medical-records.jsp").forward(request, response);
            } else {
                // Set request attributes
                request.setAttribute("userName", user.getName());
                request.setAttribute("userEmail", user.getEmail());
                
                // Forward to patients page
                request.getRequestDispatcher("/WEB-INF/views/doctor/patients.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
