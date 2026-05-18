package com.healthcare.controller;

import com.healthcare.dao.UserDAO;
import com.healthcare.model.User;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            // FIXED: pass request AND response
            redirectToDashboard(user.getRole(), request, response);
            return;
        }
        
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");
        
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Email and password are required");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }
        
        try {
            System.out.println("DEBUG: Attempting to authenticate user: " + email);
            UserDAO userDAO = new UserDAO();
            User user = userDAO.authenticateUser(email, password);
            
            System.out.println("DEBUG: Authentication result: " + (user != null ? "SUCCESS" : "FAILED"));
            if (user != null) {
                System.out.println("DEBUG: User details - ID: " + user.getId() + ", Name: " + user.getName() + ", Role: '" + user.getRole() + "'");
                HttpSession session = request.getSession();
                System.out.println("DEBUG: Creating session for user: " + user.getName());
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getId());
                session.setAttribute("userRole", user.getRole());
                session.setAttribute("userName", user.getName());
                session.setMaxInactiveInterval(30 * 60);
                System.out.println("DEBUG: Session created with attributes - userId: " + user.getId() + ", userRole: '" + user.getRole() + "'");
                
                try {
                    userDAO.updateLastLogin(user.getId());
                } catch (Exception e) {
                    System.err.println("Warning: Could not update last login: " + e.getMessage());
                }
                
                if ("on".equals(rememberMe)) {
                    javax.servlet.http.Cookie rememberCookie = new javax.servlet.http.Cookie("remember_me", email);
                    rememberCookie.setMaxAge(7 * 24 * 60 * 60);
                    rememberCookie.setPath("/");
                    rememberCookie.setHttpOnly(true);
                    response.addCookie(rememberCookie);
                }
                
                String lastVisited = (String) session.getAttribute("lastVisited");
                if (lastVisited != null && !lastVisited.isEmpty()) {
                    session.removeAttribute("lastVisited");
                    response.sendRedirect(lastVisited);
                } else {
                    // FIXED: pass request AND response
                    redirectToDashboard(user.getRole(), request, response);
                }
                
            } else {
                request.setAttribute("error", "Invalid email or password");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            }
            
        } catch (SQLException e) {
            System.err.println("SQL Error during login: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Database error occurred. Please check database connection.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("General Error during login: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "An error occurred during login: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
    
    private void redirectToDashboard(String role, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String contextPath = request.getContextPath();
        System.out.println("DEBUG: Redirecting role '" + role + "' to context path: " + contextPath);
        
        switch (role.toUpperCase()) {
            case "PATIENT":
                response.sendRedirect(contextPath + "/patient/dashboard");
                break;
            case "DOCTOR":
                response.sendRedirect(contextPath + "/doctor/dashboard");
                break;
            case "ADMIN":
                response.sendRedirect(contextPath + "/admin/dashboard");
                break;
            default:
                System.err.println("ERROR: Unknown role: " + role + " - redirecting to index.jsp");
                response.sendRedirect(contextPath + "/index.jsp");
                break;
        }
    }
}