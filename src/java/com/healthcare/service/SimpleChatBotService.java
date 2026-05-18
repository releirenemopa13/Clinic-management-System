package com.healthcare.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SimpleChatBotService {
    
    // Option A: OpenAI (requires API key)
    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    
    // Option B: Google Gemini (uses GEMINI_API_KEY environment variable)
    // The Client automatically reads the key from the environment.
    
    public String getChatResponse(int sessionId, String userMessage, int patientId) {
        // 1. Emergency check (local, fast)
        String emergency = checkEmergency(userMessage);
        if (emergency != null) return emergency;
        
        // 2. Call AI - using Google Gemini
        return callGemini(userMessage);
        // return callOpenAI(userMessage); // uncomment if you prefer OpenAI
        // return callOllama(userMessage); // old Ollama method (commented)
    }
    
    /**
     * Calls Google Gemini API using official Java SDK.
     * Requires GEMINI_API_KEY environment variable to be set.
     */
    private String callGemini(String userMessage) {
        try {
            System.out.println("=== Gemini Debug ===");
            System.out.println("User message: " + userMessage);
            
            // Create a simple HTTP request to Gemini API
            String geminiApiKey = "AIzaSyCQmX9NpQwlg4rK1HqzesZFs9K0mVqPXic";
            if (geminiApiKey == null || geminiApiKey.isEmpty()) {
                return "Google Gemini API key not configured. Please set GEMINI_API_KEY environment variable.";
            }
            
            JsonObject body = new JsonObject();
            
            JsonObject part = new JsonObject();
            part.addProperty("text", userMessage);
            
            JsonArray partsArray = new JsonArray();
            partsArray.add(part);
            
            JsonObject contents = new JsonObject();
            contents.add("parts", partsArray);
            
            JsonArray contentsArray = new JsonArray();
            contentsArray.add(contents);
            
            body.add("contents", contentsArray);
            body.add("generationConfig", new JsonObject());
            
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=" + geminiApiKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);
            
            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // Get response
            int responseCode = conn.getResponseCode();
            System.out.println("Gemini response code: " + responseCode);
            
            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    
                    System.out.println("Gemini raw response: " + response.toString());
                    
                    // Parse Gemini response
                    JsonObject responseJson = JsonParser.parseString(response.toString()).getAsJsonObject();
                    if (responseJson.has("candidates")) {
                        JsonArray candidates = responseJson.getAsJsonArray("candidates");
                        if (candidates.size() > 0) {
                            JsonObject candidate = candidates.get(0).getAsJsonObject();
                            JsonObject content = candidate.getAsJsonObject("content");
                            if (content.has("parts")) {
                                JsonArray parts = content.getAsJsonArray("parts");
                                if (parts.size() > 0) {
                                    JsonObject responsePart = parts.get(0).getAsJsonObject();
                                    if (responsePart.has("text")) {
                                        String aiResponse = responsePart.get("text").getAsString();
                                        System.out.println("Gemini response: " + aiResponse);
                                        return aiResponse;
                                    }
                                }
                            }
                        }
                    }
                    return "Sorry, I couldn't understand Gemini response.";
                }
            } else {
                System.out.println("Gemini error response: " + responseCode);
                return "Error: Gemini returned status " + responseCode + ". Please check API key and model availability.";
            }
        } catch (Exception e) {
            System.out.println("Gemini connection error: " + e.getMessage());
            e.printStackTrace();
            return "Cannot connect to Google Gemini. Please make sure GEMINI_API_KEY is set and internet is available.";
        }
    }
    
    // OpenAI method (unchanged, kept for reference)
    private String callOpenAI(String userMessage) {
        if (OPENAI_API_KEY == null || OPENAI_API_KEY.isEmpty()) {
            return "OpenAI API key not configured. Please set OPENAI_API_KEY environment variable.";
        }
        
        try {
            JsonObject body = new JsonObject();
            body.addProperty("model", "gpt-3.5-turbo");
            JsonArray messages = new JsonArray();
            JsonObject message = new JsonObject();
            message.addProperty("role", "user");
            message.addProperty("content", userMessage);
            messages.add(message);
            body.add("messages", messages);
            
            URL url = new URL(OPENAI_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
            conn.setDoOutput(true);
            
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    
                    JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                    return jsonObject.getAsJsonArray("choices")
                            .get(0).getAsJsonObject()
                            .getAsJsonObject("message")
                            .get("content").getAsString();
                }
            } else {
                return "AI service error: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Sorry, AI service is unavailable.";
        }
    }
    
    // Ollama method – kept only for reference, no longer used
    // private String callOllama(String userMessage) { ... }
    
    private String checkEmergency(String message) {
        String lower = message.toLowerCase();
        if (lower.contains("chest pain") || lower.contains("heart attack") ||
            lower.contains("emergency") || lower.contains("difficulty breathing")) {
            return "🚨 EMERGENCY: Please call emergency services immediately!";
        }
        return null;
    }
}