package com.healthcare.dao;

import com.healthcare.model.Patient;
import com.healthcare.model.PatientSummary;
import com.healthcare.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {
    
    public boolean createPatient(Patient patient) throws SQLException {
        String sql = "INSERT INTO patients (user_id, date_of_birth, blood_group, allergies, emergency_contact, address) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patient.getUserId());
            stmt.setDate(2, Date.valueOf(patient.getDateOfBirth()));
            stmt.setString(3, patient.getBloodGroup());
            stmt.setString(4, patient.getAllergies());
            stmt.setString(5, patient.getEmergencyContact());
            stmt.setString(6, patient.getAddress());
            
            return stmt.executeUpdate() > 0;
        } finally {
            DatabaseUtil.close(conn, stmt, null);
        }
    }
    
    public Patient getPatientById(int id) throws SQLException {
        String sql = "SELECT * FROM patients WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPatient(rs);
            }
            return null;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public Patient getPatientByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM patients WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPatient(rs);
            }
            return null;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<Patient> getAllPatients() throws SQLException {
        String sql = "SELECT p.*, u.name, u.email FROM patients p JOIN users u ON p.user_id = u.id WHERE u.is_active = TRUE ORDER BY u.name";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Patient> patients = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
            return patients;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public boolean updatePatient(Patient patient) throws SQLException {
        String sql = "UPDATE patients SET date_of_birth = ?, blood_group = ?, allergies = ?, emergency_contact = ?, address = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setDate(1, Date.valueOf(patient.getDateOfBirth()));
            stmt.setString(2, patient.getBloodGroup());
            stmt.setString(3, patient.getAllergies());
            stmt.setString(4, patient.getEmergencyContact());
            stmt.setString(5, patient.getAddress());
            stmt.setInt(6, patient.getId());
            
            return stmt.executeUpdate() > 0;
        } finally {
            DatabaseUtil.close(conn, stmt, null);
        }
    }
    
    public boolean deletePatient(int id) throws SQLException {
        String sql = "DELETE FROM patients WHERE id = ?";
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
    
    public List<Patient> searchPatients(String searchTerm) throws SQLException {
        String sql = "SELECT p.*, u.name, u.email FROM patients p JOIN users u ON p.user_id = u.id WHERE u.is_active = TRUE AND (u.name LIKE ? OR u.email LIKE ?) ORDER BY u.name";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Patient> patients = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
            return patients;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<PatientSummary> searchPatientSummaries(String searchTerm) throws SQLException {
        String sql = "SELECT p.id, u.name, u.email FROM patients p JOIN users u ON p.user_id = u.id "
                + "WHERE u.is_active = TRUE AND (u.name LIKE ? OR u.email LIKE ?) ORDER BY u.name";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<PatientSummary> results = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(new PatientSummary(rs.getInt("id"), rs.getString("name"), rs.getString("email")));
            }
            return results;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }

    public PatientSummary getPatientSummaryById(int id) throws SQLException {
        String sql = "SELECT p.id, u.name, u.email FROM patients p JOIN users u ON p.user_id = u.id WHERE p.id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return new PatientSummary(rs.getInt("id"), rs.getString("name"), rs.getString("email"));
            }
            return null;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }

    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setId(rs.getInt("id"));
        patient.setUserId(rs.getInt("user_id"));
        patient.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        patient.setBloodGroup(rs.getString("blood_group"));
        patient.setAllergies(rs.getString("allergies"));
        patient.setEmergencyContact(rs.getString("emergency_contact"));
        patient.setAddress(rs.getString("address"));
        return patient;
    }
}
