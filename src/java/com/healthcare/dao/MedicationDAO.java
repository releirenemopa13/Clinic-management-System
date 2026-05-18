package com.healthcare.dao;

import com.healthcare.model.Medication;
import com.healthcare.util.DatabaseUtil;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicationDAO {
    
    public boolean createMedication(Medication medication) throws SQLException {
        String sql = "INSERT INTO medications (patient_id, medicine_name, dosage, frequency, start_date, end_date, instructions, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, medication.getPatientId());
            stmt.setString(2, medication.getMedicineName());
            stmt.setString(3, medication.getDosage());
            stmt.setString(4, medication.getFrequency());
            stmt.setDate(5, Date.valueOf(medication.getStartDate()));
            if (medication.getEndDate() != null) {
                stmt.setDate(6, Date.valueOf(medication.getEndDate()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            stmt.setString(7, medication.getInstructions());
            stmt.setString(8, medication.getStatus());
            
            return stmt.executeUpdate() > 0;
        } finally {
            DatabaseUtil.close(conn, stmt, null);
        }
    }
    
    public Medication getMedicationById(int id) throws SQLException {
        String sql = "SELECT * FROM medications WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToMedication(rs);
            }
            return null;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<Medication> getMedicationsByPatientId(int patientId) throws SQLException {
        String sql = "SELECT * FROM medications WHERE patient_id = ? ORDER BY start_date DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Medication> medications = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                medications.add(mapResultSetToMedication(rs));
            }
            return medications;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<Medication> getActiveMedications(int patientId) throws SQLException {
        String sql = "SELECT * FROM medications WHERE patient_id = ? AND status = 'ACTIVE' AND (end_date IS NULL OR end_date >= CURDATE()) ORDER BY start_date DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Medication> medications = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                medications.add(mapResultSetToMedication(rs));
            }
            return medications;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<Medication> getMedicationsByStatus(int patientId, String status) throws SQLException {
        String sql = "SELECT * FROM medications WHERE patient_id = ? AND status = ? ORDER BY start_date DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Medication> medications = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);
            stmt.setString(2, status);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                medications.add(mapResultSetToMedication(rs));
            }
            return medications;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<Medication> getMedicationsDueToday(int patientId) throws SQLException {
        String sql = "SELECT * FROM medications WHERE patient_id = ? AND status = 'ACTIVE' AND start_date <= CURDATE() AND (end_date IS NULL OR end_date >= CURDATE()) ORDER BY medicine_name";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Medication> medications = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                medications.add(mapResultSetToMedication(rs));
            }
            return medications;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public boolean updateMedication(Medication medication) throws SQLException {
        String sql = "UPDATE medications SET medicine_name = ?, dosage = ?, frequency = ?, start_date = ?, end_date = ?, instructions = ?, status = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, medication.getMedicineName());
            stmt.setString(2, medication.getDosage());
            stmt.setString(3, medication.getFrequency());
            stmt.setDate(4, Date.valueOf(medication.getStartDate()));
            if (medication.getEndDate() != null) {
                stmt.setDate(5, Date.valueOf(medication.getEndDate()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            stmt.setString(6, medication.getInstructions());
            stmt.setString(7, medication.getStatus());
            stmt.setInt(8, medication.getId());
            
            return stmt.executeUpdate() > 0;
        } finally {
            DatabaseUtil.close(conn, stmt, null);
        }
    }
    
    public boolean markMedicationAsCompleted(int medicationId) throws SQLException {
        String sql = "UPDATE medications SET status = 'COMPLETED' WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, medicationId);
            
            return stmt.executeUpdate() > 0;
        } finally {
            DatabaseUtil.close(conn, stmt, null);
        }
    }
    
    public boolean deleteMedication(int id) throws SQLException {
        String sql = "DELETE FROM medications WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            
            return stmt.executeUpdate() > 0;
        } finally {
            DatabaseUtil.close(conn, stmt, null);
        }
    }
    
    public int getActiveMedicationsCount(int patientId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM medications WHERE patient_id = ? AND status = 'ACTIVE' AND (end_date IS NULL OR end_date >= CURDATE())";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    private Medication mapResultSetToMedication(ResultSet rs) throws SQLException {
        Medication medication = new Medication();
        medication.setId(rs.getInt("id"));
        medication.setPatientId(rs.getInt("patient_id"));
        medication.setMedicineName(rs.getString("medicine_name"));
        medication.setDosage(rs.getString("dosage"));
        medication.setFrequency(rs.getString("frequency"));
        
        Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            medication.setStartDate(startDate.toLocalDate());
        }
        
        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            medication.setEndDate(endDate.toLocalDate());
        }
        
        medication.setInstructions(rs.getString("instructions"));
        medication.setStatus(rs.getString("status"));
        
        return medication;
    }
}
