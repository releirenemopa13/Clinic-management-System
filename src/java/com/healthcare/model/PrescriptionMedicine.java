package com.healthcare.model;

public class PrescriptionMedicine {
    private int id;
    private int prescriptionId;
    private String medicineName;
    private String dosage;
    private String duration;
    private String instructions;

    public PrescriptionMedicine() {}

    public PrescriptionMedicine(int id, int prescriptionId, String medicineName, String dosage, String duration, String instructions) {
        this.id = id;
        this.prescriptionId = prescriptionId;
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.duration = duration;
        this.instructions = instructions;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(int prescriptionId) { this.prescriptionId = prescriptionId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
}
