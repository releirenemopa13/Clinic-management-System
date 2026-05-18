package com.healthcare.filter;

import com.healthcare.model.User;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(urlPatterns = {"/patient/*", "/doctor/*", "/admin/*"})
public class AuthFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        HttpSession session = httpRequest.getSession(false);
        User user = null;
        
        if (session != null) {
            user = (User) session.getAttribute("user");
        }
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());
        
        // Check if user is authenticated
        if (user == null) {
            // Store requested page for redirect after login
            if (session == null) {
                session = httpRequest.getSession();
            }
            session.setAttribute("lastVisited", requestURI);
            
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }
        
        // Check role-based access
        if (path.startsWith("/patient/") && !"PATIENT".equals(user.getRole())) {
            httpResponse.sendRedirect(contextPath + "/unauthorized.jsp");
            return;
        }
        
        if (path.startsWith("/doctor/") && !"DOCTOR".equals(user.getRole())) {
            httpResponse.sendRedirect(contextPath + "/unauthorized.jsp");
            return;
        }
        
        if (path.startsWith("/admin/") && !"ADMIN".equals(user.getRole())) {
            httpResponse.sendRedirect(contextPath + "/unauthorized.jsp");
            return;
        }
        
        // Check session timeout warning (5 minutes before expiry)
        if (session != null) {
            int maxInactiveInterval = session.getMaxInactiveInterval();
            long lastAccessedTime = session.getLastAccessedTime();
            long currentTime = System.currentTimeMillis();
            long timeSinceLastAccess = currentTime - lastAccessedTime;
            long timeUntilTimeout = maxInactiveInterval * 1000 - timeSinceLastAccess;
            
            // Show warning if less than 5 minutes remaining
            if (timeUntilTimeout < 5 * 60 * 1000 && timeUntilTimeout > 0) {
                httpRequest.setAttribute("sessionWarning", true);
                httpRequest.setAttribute("timeUntilTimeout", timeUntilTimeout / 1000);
            }
        }
        
        // Continue with the request
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}
