package com.healthcare.service;

import com.healthcare.dao.*;
import com.healthcare.model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class DynamicChatBotService {
    
    private MedicalAPIService medicalAPI;
    
    // Emergency keywords that require immediate attention
    private static final String[] EMERGENCY_KEYWORDS = {
        "chest pain", "difficulty breathing", "shortness of breath", "unconscious",
        "seizure", "stroke", "heart attack", "bleeding", "suicide", "emergency",
        "can't breathe", "faint", "collapse", "severe pain", "overdose"
    };
    
    public DynamicChatBotService() {
        this.medicalAPI = new MedicalAPIService();
    }
    
    // Main chat response method using API integration
    public String getChatResponse(int sessionId, String userMessage, int patientId) {
        // Check for emergency keywords first
        if (containsEmergencyKeywords(userMessage)) {
            String emergencyResponse = getEmergencyResponse();
            return emergencyResponse;
        }
        
        // Process the message and generate response using APIs
        String botResponse = processUserMessageWithAPI(userMessage);
        
        return botResponse;
    }
    
    // Create new chat session
    public int createChatSession(int patientId) {
        return 0; // Simple session ID for now
    }
    
    // Get chat history
    public List<String> getChatHistory(int sessionId) {
        return new ArrayList<>(); // Empty history for now
    }
    
    // Process user message using API integration
    private String processUserMessageWithAPI(String message) {
        String lowerMessage = message.toLowerCase().trim();
        
        // Greeting responses
        if (isGreeting(lowerMessage)) {
            return getGreetingResponse();
        }
        
        // Help/assistance requests
        if (isHelpRequest(lowerMessage)) {
            return getHelpResponse();
        }
        
        // Drug-related queries
        if (medicalAPI.isDrugQuery(lowerMessage)) {
            return getDrugResponse(message);
        }
        
        // Medical condition queries
        if (isMedicalQuery(lowerMessage)) {
            return getMedicalConditionResponse(message);
        }
        
        // General health questions
        if (isGeneralHealthQuery(lowerMessage)) {
            return getGeneralHealthResponse(lowerMessage);
        }
        
        // Default response
        return getDefaultResponse();
    }
    
    // Get drug response using OpenFDA API
    private String getDrugResponse(String message) {
        String drugName = medicalAPI.extractDrugName(message);
        
        if (drugName.isEmpty()) {
            return "I can help you with medication information. Please specify the name of the medication you're asking about.\n\n" +
                   "For example: 'What are the side effects of aspirin?' or 'Information about Tylenol'\n\n" +
                   "**Important:** Always consult your healthcare provider or pharmacist about medication questions.";
        }
        
        Map<String, Object> drugInfo = medicalAPI.getOpenFDAInfo(drugName);
        
        if (drugInfo.containsKey("error")) {
            return "I couldn't find specific information about '" + drugName + "' in the FDA database.\n\n" +
                   "This could be because:\n" +
                   "- The medication name might be spelled differently\n" +
                   "- It might be a generic medication\n" +
                   "- The medication might not be in the FDA database\n\n" +
                   "Please check the spelling and try again, or consult your healthcare provider for accurate medication information.";
        }
        
        return formatDrugResponse(drugInfo, drugName);
    }
    
    // Get medical condition response using NIH API
    private String getMedicalConditionResponse(String message) {
        // Extract medical condition from message
        String condition = extractMedicalCondition(message);
        
        if (condition.isEmpty()) {
            return "I can help you with medical information. Please tell me about the specific condition or symptom you're experiencing.\n\n" +
                   "For example: 'headache', 'fever', 'cough', 'stomach pain', etc.\n\n" +
                   "I'll provide information from authoritative medical sources like NIH and CDC.";
        }
        
        try {
            Map<String, Object> medicalInfo = medicalAPI.getComprehensiveMedicalInfo(condition);
            
            if (!medicalInfo.containsKey("primary") && !medicalInfo.containsKey("secondary")) {
                // Return fallback information if API fails
                return getFallbackMedicalInfo(condition);
            }
            
            return formatMedicalResponse(medicalInfo, condition);
        } catch (Exception e) {
            // Return fallback information if API fails
            return getFallbackMedicalInfo(condition);
        }
    }
    
    // Fallback medical information for common conditions
    private String getFallbackMedicalInfo(String condition) {
        String lowerCondition = condition.toLowerCase();
        
        switch (lowerCondition) {
            case "headache":
                return "**Headache Information**\n\n" +
                       "Headaches are one of the most common medical complaints. Most headaches are not serious and can be treated with over-the-counter pain relievers.\n\n" +
                       "**Common Types:**\n" +
                       "- Tension headaches (most common)\n" +
                       "- Migraines\n" +
                       "- Sinus headaches\n" +
                       "- Cluster headaches\n\n" +
                       "**When to Seek Medical Attention:**\n" +
                       "- Sudden, severe headache\n" +
                       "- Headache with fever, stiff neck, confusion, or seizure\n" +
                       "- Headache after head injury\n" +
                       "- Headache that worsens despite treatment\n\n" +
                       "**Self-Care:**\n" +
                       "- Rest in a quiet, dark room\n" +
                       "- Apply cold or warm compress\n" +
                       "- Stay hydrated\n" +
                       "- Over-the-counter pain relievers (aspirin, ibuprofen, acetaminophen)\n\n" +
                       "**Source:** General medical information. Always consult a healthcare provider for personalized advice.";
                       
            case "fever":
                return "**Fever Information**\n\n" +
                       "A fever is a temporary increase in body temperature, often due to an illness. A fever is a sign that your body is fighting an infection.\n\n" +
                       "**Normal Body Temperature:**\n" +
                       "- 97°F to 99°F (36.1°C to 37.2°C)\n" +
                       "- Fever is typically 100.4°F (38°C) or higher\n\n" +
                       "**Common Causes:**\n" +
                       "- Viral infections (cold, flu)\n" +
                       "- Bacterial infections\n" +
                       "- Heat exhaustion\n" +
                       "- Certain medications\n\n" +
                       "**When to Seek Medical Attention:**\n" +
                       "- Temperature above 103°F (39.4°C)\n" +
                       "- Fever lasting more than 3 days\n" +
                       "- Fever with severe headache, stiff neck, or confusion\n" +
                       "- Fever in infants under 3 months\n\n" +
                       "**Self-Care:**\n" +
                       "- Rest and stay hydrated\n" +
                       "- Dress in light clothing\n" +
                       "- Lukewarm bath\n" +
                       "- Over-the-counter fever reducers\n\n" +
                       "**Source:** General medical information. Always consult a healthcare provider for personalized advice.";
                       
            case "aspirin":
                return "**Aspirin Information**\n\n" +
                       "Aspirin is a nonsteroidal anti-inflammatory drug (NSAID) used to reduce pain, fever, or inflammation.\n\n" +
                       "**Common Uses:**\n" +
                       "- Headache and muscle pain relief\n" +
                       "- Fever reduction\n" +
                       "- Anti-inflammatory effects\n" +
                       "- Blood thinning (low-dose aspirin)\n\n" +
                       "**Side Effects:**\n" +
                       "- Stomach upset or bleeding\n" +
                       "- Increased bleeding risk\n" +
                       "- Allergic reactions (rare)\n" +
                       "- Reye's syndrome in children (avoid giving to children under 18)\n\n" +
                       "**Precautions:**\n" +
                       "- Take with food to reduce stomach upset\n" +
                       "- Avoid alcohol while taking aspirin\n" +
                       "- Inform your doctor if you take blood thinners\n" +
                       "- Do not give to children or teenagers with flu symptoms\n\n" +
                       "**Dosage:**\n" +
                       "- Follow package instructions\n" +
                       "- Typical adult dose: 325-650mg every 4-6 hours\n" +
                       "- Maximum: 4,000mg per day\n\n" +
                       "**Source:** General medication information. Always consult your healthcare provider or pharmacist.";
                       
            case "diabetes":
                return "**Diabetes Information**\n\n" +
                       "Diabetes is a chronic disease that affects how your body processes blood sugar (glucose).\n\n" +
                       "**Types of Diabetes:**\n" +
                       "- Type 1: Body doesn't produce insulin\n" +
                       "- Type 2: Body doesn't use insulin properly\n" +
                       "- Gestational: Develops during pregnancy\n\n" +
                       "**Common Symptoms:**\n" +
                       "- Increased thirst and hunger\n" +
                       "- Frequent urination\n" +
                       "- Unexplained weight loss\n" +
                       "- Fatigue\n" +
                       "- Blurred vision\n\n" +
                       "**Management:**\n" +
                       "- Blood sugar monitoring\n" +
                       "- Medication (insulin or oral drugs)\n" +
                       "- Diet and exercise\n" +
                       "- Regular medical checkups\n\n" +
                       "**Complications (if uncontrolled):**\n" +
                       "- Heart disease and stroke\n" +
                       "- Kidney disease\n" +
                       "- Nerve damage\n" +
                       "- Eye problems\n\n" +
                       "**Source:** General medical information. Diabetes requires ongoing medical supervision.";
                       
            case "asthma":
                return "**Asthma Information**\n\n" +
                       "Asthma is a chronic condition that affects the airways in your lungs, causing breathing difficulties.\n\n" +
                       "**Common Symptoms:**\n" +
                       "- Wheezing\n" +
                       "- Shortness of breath\n" +
                       "- Chest tightness\n" +
                       "- Coughing (especially at night)\n\n" +
                       "**Triggers:**\n" +
                       "- Allergens (pollen, dust mites, pet dander)\n" +
                       "- Exercise\n" +
                       "- Cold air\n" +
                       "- Respiratory infections\n" +
                       "- Smoke and pollution\n\n" +
                       "**Management:**\n" +
                       "- Quick-relief inhalers (albuterol)\n" +
                       "- Long-term control medications\n" +
                       "- Avoid triggers\n" +
                       "- Regular exercise\n" +
                       "- Action plan for attacks\n\n" +
                       "**When to Seek Emergency Care:**\n" +
                       "- Severe shortness of breath\n" +
                       "- Lips or fingernails turning blue\n" +
                       "- No improvement after using rescue inhaler\n" +
                       "- Difficulty speaking full sentences\n\n" +
                       "**Source:** General medical information. Always consult your healthcare provider for asthma management.";
                       
            default:
                return "I can provide general information about '" + condition + "'. However, for specific medical advice, please consult a healthcare provider.\n\n" +
                       "**General Recommendations:**\n" +
                       "- Get adequate rest\n" +
                       "- Stay hydrated\n" +
                       "- Monitor your symptoms\n" +
                       "- Seek medical attention if symptoms worsen or persist\n\n" +
                       "**When to See a Doctor:**\n" +
                       "- Symptoms last more than a few days\n" +
                       "- You have severe symptoms\n" +
                       "- You have underlying health conditions\n" +
                       "- You're unsure about your condition\n\n" +
                       "**Source:** General medical information. This is not a substitute for professional medical advice.";
        }
    }
    
    // Format drug response
    private String formatDrugResponse(Map<String, Object> drugInfo, String drugName) {
        StringBuilder response = new StringBuilder();
        
        response.append("Information about **").append(drugName).append("** from FDA:\n\n");
        
        if (drugInfo.containsKey("brandName")) {
            response.append("**Brand Name:** ").append(drugInfo.get("brandName")).append("\n\n");
        }
        
        if (drugInfo.containsKey("purpose")) {
            response.append("**Purpose:** ").append(drugInfo.get("purpose")).append("\n\n");
        }
        
        if (drugInfo.containsKey("dosage")) {
            response.append("**Dosage Information:** ").append(drugInfo.get("dosage")).append("\n\n");
        }
        
        if (drugInfo.containsKey("warnings")) {
            response.append("**Important Warnings:** ").append(drugInfo.get("warnings")).append("\n\n");
        }
        
        response.append("**Source:** ").append(drugInfo.get("source")).append("\n\n");
        response.append("**Medical Disclaimer:** This information is from the FDA database and is for educational purposes only. " +
                       "Always consult your healthcare provider or pharmacist before taking any medication. " +
                       "Do not use this information to replace professional medical advice.");
        
        return response.toString();
    }
    
    // Format medical condition response
    private String formatMedicalResponse(Map<String, Object> medicalInfo, String condition) {
        StringBuilder response = new StringBuilder();
        
        response.append("Information about **").append(condition).append("**:\n\n");
        
        // Primary source (usually NIH)
        if (medicalInfo.containsKey("primary")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> primary = (Map<String, Object>) medicalInfo.get("primary");
            response.append("**From ").append(primary.get("source")).append("**:\n\n");
            
            if (primary.containsKey("title")) {
                response.append("**").append(primary.get("title")).append("**\n\n");
            }
            
            if (primary.containsKey("summary")) {
                response.append("**Summary:** ").append(primary.get("summary")).append("\n\n");
            }
            
            // Add additional sections if available
            for (String key : primary.keySet()) {
                if (!key.equals("source") && !key.equals("title") && !key.equals("summary") && 
                    !key.equals("url") && !key.equals("lastUpdated")) {
                    response.append("**").append(key.replace("_", " ").toUpperCase()).append(":** ")
                           .append(primary.get(key)).append("\n\n");
                }
            }
            
            if (primary.containsKey("url")) {
                response.append("**More Information:** ").append(primary.get("url")).append("\n\n");
            }
        }
        
        // Secondary source (usually CDC)
        if (medicalInfo.containsKey("secondary")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> secondary = (Map<String, Object>) medicalInfo.get("secondary");
            response.append("**Additional Information from ").append(secondary.get("source")).append("**:\n\n");
            
            if (secondary.containsKey("summary")) {
                response.append(secondary.get("summary")).append("\n\n");
            }
            
            if (secondary.containsKey("url")) {
                response.append("**CDC Resource:** ").append(secondary.get("url")).append("\n\n");
            }
        }
        
        // Add disclaimer
        if (medicalInfo.containsKey("disclaimer")) {
            response.append("**Medical Disclaimer:** ").append(medicalInfo.get("disclaimer")).append("\n\n");
        }
        
        // Add emergency advice
        response.append("**Important:** If you are experiencing severe symptoms, seek immediate medical attention. " +
                       "This information is not a substitute for professional medical care.");
        
        return response.toString();
    }
    
    // Extract medical condition from message
    private String extractMedicalCondition(String message) {
        String[] medicalTerms = {
            "headache", "migraine", "fever", "cough", "cold", "flu", "influenza",
            "nausea", "vomiting", "diarrhea", "constipation", "stomach pain", "abdominal pain",
            "chest pain", "back pain", "joint pain", "muscle pain", "arthritis",
            "dizziness", "fatigue", "insomnia", "anxiety", "depression", "stress",
            "rash", "itching", "allergy", "asthma", "diabetes", "hypertension",
            "high blood pressure", "stroke", "heart attack", "cancer", "obesity"
        };
        
        String lowerMessage = message.toLowerCase();
        for (String term : medicalTerms) {
            if (lowerMessage.contains(term)) {
                return term;
            }
        }
        
        return "";
    }
    
    // Check if message is about medical conditions
    private boolean isMedicalQuery(String message) {
        String[] medicalKeywords = {
            "symptom", "condition", "disease", "illness", "disorder", "syndrome",
            "treatment", "cure", "diagnosis", "prevention", "cause", "risk"
        };
        
        for (String keyword : medicalKeywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        
        return !extractMedicalCondition(message).isEmpty();
    }
    
    // Check for emergency keywords
    private boolean containsEmergencyKeywords(String message) {
        String lowerMessage = message.toLowerCase();
        for (String keyword : EMERGENCY_KEYWORDS) {
            if (lowerMessage.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    // Emergency response
    private String getEmergencyResponse() {
        return "EMERGENCY: Based on your message, you may need immediate medical attention.\n\n" +
               "Please call emergency services (911) or go to the nearest emergency room immediately.\n\n" +
               "Do not wait - some symptoms require urgent medical care.\n\n" +
               "If you are unable to call for help, ask someone nearby to assist you.\n\n" +
               "This chatbot cannot handle emergency situations. Please seek immediate medical attention.";
    }
    
    // Check if message is a greeting
    private boolean isGreeting(String message) {
        return Pattern.compile("\\b(hi|hello|hey|good morning|good afternoon|good evening)\\b")
                      .matcher(message).find();
    }
    
    // Greeting response
    private String getGreetingResponse() {
        return "Hello! I'm your healthcare assistant with access to real-time medical information from authoritative sources like NIH, CDC, and FDA.\n\n" +
               "I can help you with:\n\n" +
               "Medical condition information from NIH MedlinePlus\n" +
               "Drug information from FDA databases\n" +
               "Health guidelines from CDC\n" +
               "Evidence-based medical information\n\n" +
               "Please describe your symptoms or ask about a medical condition, and I'll provide current information from reliable medical sources.\n\n" +
               "Remember: This information is for educational purposes and is not a substitute for professional medical advice.";
    }
    
    // Check if message is a help request
    private boolean isHelpRequest(String message) {
        return Pattern.compile("\\b(help|what can you do|how can you help|assist)\\b")
                      .matcher(message).find();
    }
    
    // Help response
    private String getHelpResponse() {
        return "I'm here to help with your health questions using real-time medical information! Here's what I can do:\n\n" +
               "**Medical Conditions:** Get information from NIH MedlinePlus database\n" +
               "**Drug Information:** Access FDA drug labels and safety information\n" +
               "**Health Guidelines:** CDC recommendations and public health information\n" +
               "**Evidence-Based Information:** Current medical data from authoritative sources\n\n" +
               "Simply tell me about your symptoms, ask about a medication, or inquire about a health condition.\n\n" +
               "Important: I'm not a substitute for professional medical care. " +
               "For serious symptoms, always consult a healthcare provider.";
    }
    
    // Check if message is a general health query
    private boolean isGeneralHealthQuery(String message) {
        return Pattern.compile("\\b(health|wellness|nutrition|exercise|sleep|stress|diet)\\b")
                      .matcher(message).find();
    }
    
    // General health response
    private String getGeneralHealthResponse(String message) {
        return "That's a great question about general health! Let me get you current information from authoritative health sources.\n\n" +
               "Based on public health guidelines from organizations like CDC and NIH:\n\n" +
               "**Nutrition:** Balanced diet with fruits, vegetables, whole grains, lean proteins\n" +
               "**Exercise:** 150 minutes of moderate activity per week for adults\n" +
               "**Sleep:** 7-9 hours of quality sleep per night for adults\n" +
               "**Hydration:** Adequate water intake throughout the day\n" +
               "**Stress Management:** Regular relaxation and stress-reduction techniques\n\n" +
               "For specific health concerns or symptoms, please let me know what you're experiencing, " +
               "and I can provide more targeted information from current medical databases.\n\n" +
               "Always consult healthcare professionals for personalized medical advice.";
    }
    
    // Default response
    private String getDefaultResponse() {
        return "I'm here to help with your health questions using real-time medical information! I can access:\n\n" +
               "NIH MedlinePlus for medical conditions and symptoms\n" +
               "FDA databases for medication information\n" +
               "CDC guidelines for public health information\n\n" +
               "Please tell me more about your health concerns, symptoms, or ask about a specific condition or medication, " +
               "and I'll provide current information from authoritative medical sources.\n\n" +
               "For serious medical issues, always consult a healthcare provider.";
    }
}
