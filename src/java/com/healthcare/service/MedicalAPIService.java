package com.healthcare.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class MedicalAPIService {
    
    private static final String NIH_API_BASE = "https://medlineplus.gov/api/healthtopics";
    private static final String CDC_API_BASE = "https://tools.cdc.gov/api";
    private static final String OPENFDA_API_BASE = "https://api.fda.gov/drug";
    
    // Get medical information from NIH MedlinePlus
    public Map<String, Object> getNIHMedicalInfo(String condition) {
        Map<String, Object> medicalInfo = new HashMap<>();
        
        try {
            // Search MedlinePlus for the condition
            String encodedCondition = URLEncoder.encode(condition, "UTF-8");
            String apiUrl = NIH_API_BASE + "/search.json?query=" + encodedCondition + "&pageSize=5";
            
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "HealthCarePlus/1.0");
            
            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                // Parse JSON response using simple string parsing
                String jsonResponse = response.toString();
                
                // Simple JSON parsing for healthTopics array
                if (jsonResponse.contains("\"healthTopics\"")) {
                    int topicsStart = jsonResponse.indexOf("\"healthTopics\":[");
                    if (topicsStart != -1) {
                        int topicsEnd = jsonResponse.indexOf("]", topicsStart);
                        String topicsArray = jsonResponse.substring(topicsStart + 16, topicsEnd + 1);
                        
                        // Extract first topic object
                        int firstTopicStart = topicsArray.indexOf("{");
                        if (firstTopicStart != -1) {
                            int firstTopicEnd = findMatchingBrace(topicsArray, firstTopicStart);
                            String firstTopicStr = topicsArray.substring(firstTopicStart, firstTopicEnd + 1);
                            
                            medicalInfo.put("source", "NIH MedlinePlus");
                            medicalInfo.put("title", extractJsonValue(firstTopicStr, "title", condition));
                            medicalInfo.put("summary", extractJsonValue(firstTopicStr, "summary", "No summary available"));
                            medicalInfo.put("url", extractJsonValue(firstTopicStr, "url", ""));
                            medicalInfo.put("lastUpdated", extractJsonValue(firstTopicStr, "lastUpdated", ""));
                            
                            // Get detailed information
                            String topicUrl = extractJsonValue(firstTopicStr, "url", "");
                            if (!topicUrl.isEmpty()) {
                                String detailUrl = topicUrl.replace(".json", ".json?full=true");
                                Map<String, Object> details = getNIHDetails(detailUrl);
                                medicalInfo.putAll(details);
                            }
                        }
                    }
                }
            } else {
                // Handle non-200 response codes
                medicalInfo.put("error", "Unable to fetch medical information - server returned error");
            }
            
            connection.disconnect();
            
        } catch (Exception e) {
            System.err.println("Error fetching NIH data: " + e.getMessage());
            medicalInfo.put("error", "Unable to fetch medical information from NIH");
        }
        
        return medicalInfo;
    }
    
    // Get detailed information from NIH
    private Map<String, Object> getNIHDetails(String detailUrl) {
        Map<String, Object> details = new HashMap<>();
        
        try {
            URL url = new URL(detailUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "HealthCarePlus/1.0");
            
            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                String jsonResponse = response.toString();
                
                // Extract sections using simple JSON parsing
                if (jsonResponse.contains("\"sections\"")) {
                    int sectionsStart = jsonResponse.indexOf("\"sections\":[");
                    if (sectionsStart != -1) {
                        int sectionsEnd = jsonResponse.indexOf("]", sectionsStart);
                        String sectionsArray = jsonResponse.substring(sectionsStart + 12, sectionsEnd + 1);
                        
                        // Parse individual sections
                        int sectionStart = sectionsArray.indexOf("{");
                        while (sectionStart != -1 && sectionStart < sectionsArray.length()) {
                            int sectionEnd = findMatchingBrace(sectionsArray, sectionStart);
                            if (sectionEnd >= sectionsArray.length()) break;
                            
                            String sectionStr = sectionsArray.substring(sectionStart, sectionEnd + 1);
                            String sectionTitle = extractJsonValue(sectionStr, "title", "");
                            String sectionContent = extractJsonValue(sectionStr, "content", "");
                            
                            if (!sectionTitle.isEmpty() && !sectionContent.isEmpty()) {
                                details.put(sectionTitle.toLowerCase().replace(" ", "_"), sectionContent);
                            }
                            
                            // Find next section
                            sectionStart = sectionsArray.indexOf("{", sectionEnd + 1);
                        }
                    }
                }
            }
            
            connection.disconnect();
            
        } catch (Exception e) {
            System.err.println("Error fetching NIH details: " + e.getMessage());
        }
        
        return details;
    }
    
    // Get CDC health information
    public Map<String, Object> getCDCHealthInfo(String condition) {
        Map<String, Object> medicalInfo = new HashMap<>();
        
        try {
            // CDC API implementation (simplified - CDC doesn't have a public API for general health info)
            // We'll use CDC's RSS feeds or web scraping for CDC content
            
            String encodedCondition = URLEncoder.encode(condition, "UTF-8");
            String searchUrl = "https://www.cdc.gov/search-results/?query=" + encodedCondition;
            
            // For demonstration, we'll create a structured response
            medicalInfo.put("source", "CDC - Centers for Disease Control and Prevention");
            medicalInfo.put("title", "CDC Information on " + condition);
            medicalInfo.put("summary", "Visit CDC.gov for authoritative information on " + condition);
            medicalInfo.put("url", searchUrl);
            medicalInfo.put("disclaimer", "CDC provides evidence-based health information");
            
        } catch (Exception e) {
            System.err.println("Error fetching CDC data: " + e.getMessage());
            medicalInfo.put("error", "Unable to fetch CDC information");
        }
        
        return medicalInfo;
    }
    
    // Get drug information from OpenFDA
    public Map<String, Object> getOpenFDAInfo(String drugName) {
        Map<String, Object> drugInfo = new HashMap<>();
        
        try {
            String encodedDrug = URLEncoder.encode(drugName, "UTF-8");
            String apiUrl = OPENFDA_API_BASE + "/label.json?search=openfda.brand_name.exact:" + encodedDrug + "&limit=1";
            
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "HealthCarePlus/1.0");
            
            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                String jsonResponse = response.toString();
                
                // Parse results array using simple JSON parsing
                if (jsonResponse.contains("\"results\"")) {
                    int resultsStart = jsonResponse.indexOf("\"results\":[");
                    if (resultsStart != -1) {
                        int resultsEnd = jsonResponse.indexOf("]", resultsStart);
                        String resultsArray = jsonResponse.substring(resultsStart + 11, resultsEnd + 1);
                        
                        // Extract first drug object
                        int firstDrugStart = resultsArray.indexOf("{");
                        if (firstDrugStart != -1) {
                            int firstDrugEnd = findMatchingBrace(resultsArray, firstDrugStart);
                            String drugStr = resultsArray.substring(firstDrugStart, firstDrugEnd + 1);
                            
                            drugInfo.put("source", "FDA - Food and Drug Administration");
                            
                            // Extract brand name from openfda object
                            String brandName = extractJsonValue(drugStr, "brand_name", drugName);
                            if (brandName.equals(drugName)) {
                                // Try to extract from openfda nested object
                                int openfdaStart = drugStr.indexOf("\"openfda\":{");
                                if (openfdaStart != -1) {
                                    int openfdaEnd = findMatchingBrace(drugStr, openfdaStart + 10);
                                    String openfdaStr = drugStr.substring(openfdaStart, openfdaEnd + 1);
                                    brandName = extractJsonValue(openfdaStr, "brand_name", drugName);
                                }
                            }
                            drugInfo.put("brandName", brandName);
                            
                            // Extract other fields
                            drugInfo.put("purpose", extractJsonValue(drugStr, "purpose", ""));
                            drugInfo.put("warnings", extractJsonValue(drugStr, "warnings", ""));
                            drugInfo.put("dosage", extractJsonValue(drugStr, "dosage_and_administration", ""));
                        }
                    }
                }
            }
            
            connection.disconnect();
            
        } catch (Exception e) {
            System.err.println("Error fetching OpenFDA data: " + e.getMessage());
            drugInfo.put("error", "Unable to fetch FDA drug information");
        }
        
        return drugInfo;
    }
    
    // Get general medical information from multiple sources
    public Map<String, Object> getComprehensiveMedicalInfo(String query) {
        Map<String, Object> comprehensiveInfo = new HashMap<>();
        
        // Try NIH first
        Map<String, Object> nihInfo = getNIHMedicalInfo(query);
        if (!nihInfo.containsKey("error")) {
            comprehensiveInfo.put("primary", nihInfo);
        }
        
        // Try CDC
        Map<String, Object> cdcInfo = getCDCHealthInfo(query);
        if (!cdcInfo.containsKey("error")) {
            comprehensiveInfo.put("secondary", cdcInfo);
        }
        
        // Add medical disclaimer
        comprehensiveInfo.put("disclaimer", "This information is for educational purposes only and is not a substitute for professional medical advice. Always consult with a qualified healthcare provider for medical diagnosis and treatment.");
        
        return comprehensiveInfo;
    }
    
    // Check if query contains drug-related keywords
    public boolean isDrugQuery(String query) {
        String lowerQuery = query.toLowerCase();
        String[] drugKeywords = {"medicine", "medication", "drug", "pill", "tablet", "prescription", "dosage", "side effects"};
        
        for (String keyword : drugKeywords) {
            if (lowerQuery.contains(keyword)) {
                return true;
            }
        }
        
        return false;
    }
    
    // Extract potential drug name from query
    public String extractDrugName(String query) {
        // Simple extraction - in real implementation, this would be more sophisticated
        String[] words = query.split("\\s+");
        for (String word : words) {
            if (word.length() > 3 && !isCommonWord(word.toLowerCase())) {
                return word;
            }
        }
        return "";
    }
    
    // Check if word is common (not a drug name)
    private boolean isCommonWord(String word) {
        String[] commonWords = {"what", "is", "the", "for", "about", "how", "does", "can", "i", "take", "get", "have"};
        for (String common : commonWords) {
            if (common.equals(word)) {
                return true;
            }
        }
        return false;
    }
    
    // Helper method to find matching brace for JSON parsing
    private int findMatchingBrace(String str, int start) {
        int braceCount = 0;
        for (int i = start; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '{') {
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    return i;
                }
            }
        }
        return str.length() - 1;
    }
    
    // Helper method to extract JSON value by key
    private String extractJsonValue(String jsonStr, String key, String defaultValue) {
        String searchKey = "\"" + key + "\":";
        int keyIndex = jsonStr.indexOf(searchKey);
        if (keyIndex == -1) {
            return defaultValue;
        }
        
        int valueStart = keyIndex + searchKey.length();
        
        // Skip whitespace
        while (valueStart < jsonStr.length() && Character.isWhitespace(jsonStr.charAt(valueStart))) {
            valueStart++;
        }
        
        if (valueStart >= jsonStr.length()) {
            return defaultValue;
        }
        
        char firstChar = jsonStr.charAt(valueStart);
        
        if (firstChar == '"') {
            // String value
            valueStart++;
            int valueEnd = jsonStr.indexOf("\"", valueStart);
            if (valueEnd == -1) {
                return defaultValue;
            }
            return jsonStr.substring(valueStart, valueEnd);
        } else {
            // Number or boolean value
            int valueEnd = valueStart;
            while (valueEnd < jsonStr.length() && 
                   (Character.isLetterOrDigit(jsonStr.charAt(valueEnd)) || 
                    jsonStr.charAt(valueEnd) == '.' || 
                    jsonStr.charAt(valueEnd) == '-')) {
                valueEnd++;
            }
            return jsonStr.substring(valueStart, valueEnd);
        }
    }
}
