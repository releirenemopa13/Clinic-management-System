package com.healthcare.servlet.patient;

import com.healthcare.model.User;
import com.healthcare.service.DynamicChatBotService;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ChatServlet extends HttpServlet {
    
    private DynamicChatBotService chatBotService;
    
    @Override
    public void init() throws ServletException {
        chatBotService = new DynamicChatBotService();
    }
    
    @Override
 
protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    // Forward to a JSP page that displays the chat interface
    request.getRequestDispatcher("/WEB-INF/views/patient/chat.jsp").forward(request, response);
}
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
       
        }
        
        String patientId = String.valueOf(user.getId());
        String userMessage = null;
        
        // --- 1. Try to read JSON from request body ---
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        String body = sb.toString();
        if (body != null && !body.isEmpty()) {
            try {
                JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                if (json.has("message")) userMessage = json.get("message").getAsString();
                else if (json.has("msg")) userMessage = json.get("msg").getAsString();
                else if (json.has("text")) userMessage = json.get("text").getAsString();
                else if (json.has("userMessage")) userMessage = json.get("userMessage").getAsString();
            } catch (Exception e) {
                // Not JSON – ignore
            }
        }
        
        // --- 2. Fallback to parameter reading (form-urlencoded) ---
        if (userMessage == null) {
            userMessage = request.getParameter("message");
            if (userMessage == null) userMessage = request.getParameter("msg");
            if (userMessage == null) userMessage = request.getParameter("text");
            if (userMessage == null) userMessage = request.getParameter("userMessage");
        }
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        if (userMessage == null || userMessage.trim().isEmpty()) {
            sendJsonResponse(response, "error", "Please enter a message.");
            return;
        }
        
        try {
            String botResponse = chatBotService.getChatResponse(0, userMessage, Integer.parseInt(patientId));
            sendJsonResponse(response, "success", botResponse);
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(response, "error", "AI service error. Please try again later.");
        }
    }
    
    private void sendJsonResponse(HttpServletResponse response, String status, String message)
            throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"status\":\"").append(status).append("\",");
        json.append("\"response\":\"").append(message.replace("\"", "\\\"").replace("\n", "\\n")).append("\"");
        json.append("}");
        
        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();
    }
}