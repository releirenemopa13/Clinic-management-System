package com.healthcare.model;

public class Doctor {
    private int id;
    private int userId;
    private String specialization;
    private String qualification;
    private int experience;
    private double consultationFee;
    private String availableDays;
    private String workingHours;
    private boolean isActive;

    public Doctor() {}

    public Doctor(int id, int userId, String specialization, String qualification, int experience, double consultationFee, String availableDays, String workingHours, boolean isActive) {
        this.id = id;
        this.userId = userId;
        this.specialization = specialization;
        this.qualification = qualification;
        this.experience = experience;
        this.consultationFee = consultationFee;
        this.availableDays = availableDays;
        this.workingHours = workingHours;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }

    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }

    public String getAvailableDays() { return availableDays; }
    public void setAvailableDays(String availableDays) { this.availableDays = availableDays; }

    public String getWorkingHours() { return workingHours; }
    public void setWorkingHours(String workingHours) { this.workingHours = workingHours; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
