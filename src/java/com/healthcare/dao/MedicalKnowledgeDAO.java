package com.healthcare.dao;

import com.healthcare.model.MedicalKnowledge;
import com.healthcare.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalKnowledgeDAO {
    
    // Search medical knowledge by symptom
    public List<MedicalKnowledge> searchBySymptom(String symptom) {
        List<MedicalKnowledge> knowledgeList = new ArrayList<>();
        String sql = "SELECT * FROM medical_knowledge WHERE symptom LIKE ? OR category LIKE ? ORDER BY severity DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + symptom + "%");
            pstmt.setString(2, "%" + symptom + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    knowledgeList.add(extractMedicalKnowledge(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching medical knowledge: " + e.getMessage());
            // Return empty list if database is not available
            // This allows the chatbot to work with fallback responses
        }
        
        return knowledgeList;
    }
    
    // Get all medical knowledge categories
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM medical_knowledge ORDER BY category";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting categories: " + e.getMessage());
            // Return empty list if database is not available
        }
        
        return categories;
    }
    
    // Get emergency indicators for a symptom
    public List<String> getEmergencyIndicators(String symptom) {
        List<String> indicators = new ArrayList<>();
        String sql = "SELECT emergency_indicators FROM medical_knowledge WHERE symptom LIKE ? AND emergency_indicators IS NOT NULL";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + symptom + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String emergencyText = rs.getString("emergency_indicators");
                    if (emergencyText != null && !emergencyText.trim().isEmpty()) {
                        String[] items = emergencyText.split(", ");
                        for (String item : items) {
                            indicators.add(item.trim());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting emergency indicators: " + e.getMessage());
            // Return empty list if database is not available
        }
        
        return indicators;
    }
    
    // Check if symptom indicates emergency
    public boolean isEmergencySymptom(String symptom) {
        String sql = "SELECT COUNT(*) FROM medical_knowledge WHERE symptom LIKE ? AND severity = 'emergency'";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + symptom + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking emergency symptom: " + e.getMessage());
            // Return false if database is not available
        }
        
        return false;
    }
    
    // Get self-care advice for symptom
    public String getSelfCareAdvice(String symptom) {
        String sql = "SELECT self_care_advice FROM medical_knowledge WHERE symptom LIKE ? AND self_care_advice IS NOT NULL LIMIT 1";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + symptom + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("self_care_advice");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting self-care advice: " + e.getMessage());
            // Return default advice if database is not available
        }
        
        return "Please rest and stay hydrated. If symptoms persist, consult a healthcare provider.";
    }
    
    // Get when to see doctor advice
    public String getWhenToSeeDoctor(String symptom) {
        String sql = "SELECT when_to_see_doctor FROM medical_knowledge WHERE symptom LIKE ? AND when_to_see_doctor IS NOT NULL LIMIT 1";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + symptom + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("when_to_see_doctor");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting when to see doctor advice: " + e.getMessage());
            // Return default advice if database is not available
        }
        
        return "Consult a healthcare provider if symptoms are severe or persist for more than a few days.";
    }
    
    // Helper method to extract MedicalKnowledge from ResultSet
    private MedicalKnowledge extractMedicalKnowledge(ResultSet rs) throws SQLException {
        MedicalKnowledge knowledge = new MedicalKnowledge();
        knowledge.setId(rs.getInt("id"));
        knowledge.setCategory(rs.getString("category"));
        knowledge.setSymptom(rs.getString("symptom"));
        knowledge.setConditionName(rs.getString("condition_name"));
        knowledge.setSeverity(rs.getString("severity"));
        knowledge.setSelfCareAdvice(rs.getString("self_care_advice"));
        knowledge.setWhenToSeeDoctor(rs.getString("when_to_see_doctor"));
        knowledge.setEmergencyIndicators(rs.getString("emergency_indicators"));
        knowledge.setCreatedAt(rs.getTimestamp("created_at"));
        knowledge.setUpdatedAt(rs.getTimestamp("updated_at"));
        return knowledge;
    }
}
