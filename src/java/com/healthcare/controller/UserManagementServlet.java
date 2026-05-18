package com.healthcare.controller;

import com.healthcare.model.User;
import com.healthcare.dao.UserDAO;
import com.healthcare.dao.PatientDAO;
import com.healthcare.dao.DoctorDAO;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UserManagementServlet extends HttpServlet {
    
    private UserDAO userDAO = new UserDAO();
    
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
            UserDAO userDAO = new UserDAO();
            
            if ("add".equals(action)) {
                request.getRequestDispatcher("/WEB-INF/views/admin/add-user.jsp").forward(request, response);
            } else if ("edit".equals(action)) {
                int userId = Integer.parseInt(request.getParameter("id"));
                User editUser = userDAO.getUserById(userId);
                request.setAttribute("user", editUser);
                request.getRequestDispatcher("/WEB-INF/views/admin/edit-user.jsp").forward(request, response);
            } else if ("delete".equals(action)) {
                int userId = Integer.parseInt(request.getParameter("id"));
                if (userDAO.deactivateUser(userId)) {
                    session.setAttribute("success", "User deactivated successfully!");
                } else {
                    session.setAttribute("error", "Failed to deactivate user.");
                }
                response.sendRedirect("users");
            } else if ("reset".equals(action)) {
                int userId = Integer.parseInt(request.getParameter("id"));
                String newPassword = generateTemporaryPassword();
                
                if (userDAO.updatePassword(userId, newPassword)) {
                    session.setAttribute("success", "Password reset successfully! New password: " + newPassword);
                } else {
                    session.setAttribute("error", "Failed to reset password.");
                }
                response.sendRedirect("users");
            } else {
                // Default: show all users
                String search = request.getParameter("search");
                List<User> users;
                
                if (search != null && !search.trim().isEmpty()) {
                    users = userDAO.searchUsers(search);
                } else {
                    users = userDAO.getAllUsers();
                }
                
                request.setAttribute("users", users);
                request.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(request, response);
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
            
            if ("save".equals(action)) {
                User newUser = new User();
                newUser.setName(request.getParameter("name"));
                newUser.setEmail(request.getParameter("email"));
                newUser.setPassword(request.getParameter("password"));
                newUser.setRole(request.getParameter("role"));
                newUser.setPhone(request.getParameter("phone"));
                newUser.setActive(true);
                
                UserDAO userDAO = new UserDAO();
                
                if (userDAO.isEmailExists(newUser.getEmail())) {
                    session.setAttribute("error", "Email already exists!");
                    request.setAttribute("user", newUser);
                    request.getRequestDispatcher("/WEB-INF/views/admin/add-user.jsp").forward(request, response);
                    return;
                }
                
                int userId = userDAO.createUser(newUser);
                
                if (userId > 0) {
                    // Create role-specific profile
                    if ("PATIENT".equals(newUser.getRole())) {
                        PatientDAO patientDAO = new PatientDAO();
                        // Create patient profile with default values
                        // Admin would need to fill in the details later
                    } else if ("DOCTOR".equals(newUser.getRole())) {
                        DoctorDAO doctorDAO = new DoctorDAO();
                        // Create doctor profile with default values
                        // Admin would need to fill in the details later
                    }
                    
                    session.setAttribute("success", "User created successfully!");
                } else {
                    session.setAttribute("error", "Failed to create user.");
                }
                
                response.sendRedirect("users");
            } else if ("update".equals(action)) {
                int userId = Integer.parseInt(request.getParameter("id"));
                User updateUser = userDAO.getUserById(userId);
                
                if (updateUser != null) {
                    updateUser.setName(request.getParameter("name"));
                    updateUser.setEmail(request.getParameter("email"));
                    updateUser.setPhone(request.getParameter("phone"));
                    updateUser.setActive(Boolean.parseBoolean(request.getParameter("isActive")));
                    
                    UserDAO userDAO = new UserDAO();
                    if (userDAO.updateUser(updateUser)) {
                        session.setAttribute("success", "User updated successfully!");
                    } else {
                        session.setAttribute("error", "Failed to update user.");
                    }
                }
                
                response.sendRedirect("users");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "An error occurred while processing your request.");
            response.sendRedirect("users");
        }
    }
    
    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }
}
