package com.healthcare.model;

import java.time.LocalDate;

public class Patient {
    private int id;
    private int userId;
    private LocalDate dateOfBirth;
    private String bloodGroup;
    private String allergies;
    private String emergencyContact;
    private String address;

    public Patient() {}

    public Patient(int id, int userId, LocalDate dateOfBirth, String bloodGroup, String allergies, String emergencyContact, String address) {
        this.id = id;
        this.userId = userId;
        this.dateOfBirth = dateOfBirth;
        this.bloodGroup = bloodGroup;
        this.allergies = allergies;
        this.emergencyContact = emergencyContact;
        this.address = address;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
