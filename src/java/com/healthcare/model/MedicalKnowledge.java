package com.healthcare.model;

import java.sql.Timestamp;

public class MedicalKnowledge {
    private int id;
    private String category;
    private String symptom;
    private String conditionName;
    private String severity;
    private String selfCareAdvice;
    private String whenToSeeDoctor;
    private String emergencyIndicators;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructors
    public MedicalKnowledge() {}

    public MedicalKnowledge(String category, String symptom, String conditionName, String severity) {
        this.category = category;
        this.symptom = symptom;
        this.conditionName = conditionName;
        this.severity = severity;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getSelfCareAdvice() {
        return selfCareAdvice;
    }

    public void setSelfCareAdvice(String selfCareAdvice) {
        this.selfCareAdvice = selfCareAdvice;
    }

    public String getWhenToSeeDoctor() {
        return whenToSeeDoctor;
    }

    public void setWhenToSeeDoctor(String whenToSeeDoctor) {
        this.whenToSeeDoctor = whenToSeeDoctor;
    }

    public String getEmergencyIndicators() {
        return emergencyIndicators;
    }

    public void setEmergencyIndicators(String emergencyIndicators) {
        this.emergencyIndicators = emergencyIndicators;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods
    public boolean isEmergency() {
        return "emergency".equals(severity);
    }

    public boolean isHighSeverity() {
        return "high".equals(severity) || "emergency".equals(severity);
    }

    public boolean isLowSeverity() {
        return "low".equals(severity);
    }

    @Override
    public String toString() {
        return "MedicalKnowledge{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", symptom='" + symptom + '\'' +
                ", conditionName='" + conditionName + '\'' +
                ", severity='" + severity + '\'' +
                '}';
    }
}
