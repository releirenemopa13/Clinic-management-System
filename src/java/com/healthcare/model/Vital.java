package com.healthcare.model;

import java.time.LocalDateTime;

public class Vital {
    private int id;
    private int patientId;
    private int bpSystolic;
    private int bpDiastolic;
    private int heartRate;
    private double bloodSugar;
    private double weight;
    private double temperature;
    private LocalDateTime recordedAt;

    public Vital() {}

    public Vital(int id, int patientId, int bpSystolic, int bpDiastolic, int heartRate, double bloodSugar, double weight, double temperature, LocalDateTime recordedAt) {
        this.id = id;
        this.patientId = patientId;
        this.bpSystolic = bpSystolic;
        this.bpDiastolic = bpDiastolic;
        this.heartRate = heartRate;
        this.bloodSugar = bloodSugar;
        this.weight = weight;
        this.temperature = temperature;
        this.recordedAt = recordedAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getBpSystolic() { return bpSystolic; }
    public void setBpSystolic(int bpSystolic) { this.bpSystolic = bpSystolic; }

    public int getBpDiastolic() { return bpDiastolic; }
    public void setBpDiastolic(int bpDiastolic) { this.bpDiastolic = bpDiastolic; }

    public int getHeartRate() { return heartRate; }
    public void setHeartRate(int heartRate) { this.heartRate = heartRate; }

    public double getBloodSugar() { return bloodSugar; }
    public void setBloodSugar(double bloodSugar) { this.bloodSugar = bloodSugar; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
}
