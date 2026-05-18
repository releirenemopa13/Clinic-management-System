package com.healthcare.dao;

import com.healthcare.model.Prescription;
import com.healthcare.util.DatabaseUtil;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionDAO {
    
    public boolean createPrescription(Prescription prescription) throws SQLException {
        String sql = "INSERT INTO prescriptions (appointment_id, doctor_id, patient_id, diagnosis, notes) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, prescription.getAppointmentId());
            stmt.setInt(2, prescription.getDoctorId());
            stmt.setInt(3, prescription.getPatientId());
            stmt.setString(4, prescription.getDiagnosis());
            stmt.setString(5, prescription.getNotes());
            
            return stmt.executeUpdate() > 0;
        } finally {
            DatabaseUtil.close(conn, stmt, null);
        }
    }
    
    public Prescription getPrescriptionById(int id) throws SQLException {
        String sql = "SELECT p.*, a.date as appointment_date, a.time as appointment_time, " +
                    "doc.name as doctor_name, pat.name as patient_name " +
                    "FROM prescriptions p " +
                    "JOIN appointments a ON p.appointment_id = a.id " +
                    "JOIN doctors d ON p.doctor_id = d.id " +
                    "JOIN users doc ON d.user_id = doc.id " +
                    "JOIN patients pa ON p.patient_id = pa.id " +
                    "JOIN users pat ON pa.user_id = pat.id " +
                    "WHERE p.id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPrescription(rs);
            }
            return null;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public Prescription getPrescriptionByAppointmentId(int appointmentId) throws SQLException {
        String sql = "SELECT p.*, a.date as appointment_date, a.time as appointment_time, " +
                    "doc.name as doctor_name, pat.name as patient_name " +
                    "FROM prescriptions p " +
                    "JOIN appointments a ON p.appointment_id = a.id " +
                    "JOIN doctors d ON p.doctor_id = d.id " +
                    "JOIN users doc ON d.user_id = doc.id " +
                    "JOIN patients pa ON p.patient_id = pa.id " +
                    "JOIN users pat ON pa.user_id = pat.id " +
                    "WHERE p.appointment_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, appointmentId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPrescription(rs);
            }
            return null;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<Prescription> getPrescriptionsByPatientId(int patientId) throws SQLException {
        String sql = "SELECT p.*, a.date as appointment_date, a.time as appointment_time, " +
                    "doc.name as doctor_name, pat.name as patient_name " +
                    "FROM prescriptions p " +
                    "JOIN appointments a ON p.appointment_id = a.id " +
                    "JOIN doctors d ON p.doctor_id = d.id " +
                    "JOIN users doc ON d.user_id = doc.id " +
                    "JOIN patients pa ON p.patient_id = pa.id " +
                    "JOIN users pat ON pa.user_id = pat.id " +
                    "WHERE p.patient_id = ? ORDER BY p.prescribed_at DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Prescription> prescriptions = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                prescriptions.add(mapResultSetToPrescription(rs));
            }
            return prescriptions;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<Prescription> getPrescriptionsByDoctorId(int doctorId) throws SQLException {
        String sql = "SELECT p.*, a.date as appointment_date, a.time as appointment_time, " +
                    "doc.name as doctor_name, pat.name as patient_name " +
                    "FROM prescriptions p " +
                    "JOIN appointments a ON p.appointment_id = a.id " +
                    "JOIN doctors d ON p.doctor_id = d.id " +
                    "JOIN users doc ON d.user_id = doc.id " +
                    "JOIN patients pa ON p.patient_id = pa.id " +
                    "JOIN users pat ON pa.user_id = pat.id " +
                    "WHERE p.doctor_id = ? ORDER BY p.prescribed_at DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Prescription> prescriptions = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, doctorId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                prescriptions.add(mapResultSetToPrescription(rs));
            }
            return prescriptions;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<Prescription> getAllPrescriptions() throws SQLException {
        String sql = "SELECT p.*, a.date as appointment_date, a.time as appointment_time, " +
                    "doc.name as doctor_name, pat.name as patient_name " +
                    "FROM prescriptions p " +
                    "JOIN appointments a ON p.appointment_id = a.id " +
                    "JOIN doctors d ON p.doctor_id = d.id " +
                    "JOIN users doc ON d.user_id = doc.id " +
                    "JOIN patients pa ON p.patient_id = pa.id " +
                    "JOIN users pat ON pa.user_id = pat.id " +
                    "ORDER BY p.prescribed_at DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Prescription> prescriptions = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                prescriptions.add(mapResultSetToPrescription(rs));
            }
            return prescriptions;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<Prescription> getPrescriptionsByDateRange(String startDate, String endDate) throws SQLException {
        String sql = "SELECT p.*, a.date as appointment_date, a.time as appointment_time, " +
                    "doc.name as doctor_name, pat.name as patient_name " +
                    "FROM prescriptions p " +
                    "JOIN appointments a ON p.appointment_id = a.id " +
                    "JOIN doctors d ON p.doctor_id = d.id " +
                    "JOIN users doc ON d.user_id = doc.id " +
                    "JOIN patients pa ON p.patient_id = pa.id " +
                    "JOIN users pat ON pa.user_id = pat.id " +
                    "WHERE DATE(p.prescribed_at) BETWEEN ? AND ? ORDER BY p.prescribed_at DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Prescription> prescriptions = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                prescriptions.add(mapResultSetToPrescription(rs));
            }
            return prescriptions;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public boolean updatePrescription(Prescription prescription) throws SQLException {
        String sql = "UPDATE prescriptions SET diagnosis = ?, notes = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, prescription.getDiagnosis());
            stmt.setString(2, prescription.getNotes());
            stmt.setInt(3, prescription.getId());
            
            return stmt.executeUpdate() > 0;
        } finally {
            DatabaseUtil.close(conn, stmt, null);
        }
    }
    
    public boolean deletePrescription(int id) throws SQLException {
        String sql = "DELETE FROM prescriptions WHERE id = ?";
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
    
    public int getTotalPrescriptionsCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM prescriptions";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    private Prescription mapResultSetToPrescription(ResultSet rs) throws SQLException {
        Prescription prescription = new Prescription();
        prescription.setId(rs.getInt("id"));
        prescription.setPatientId(rs.getInt("patient_id"));
        prescription.setDoctorId(rs.getInt("doctor_id"));
        prescription.setAppointmentId(rs.getInt("appointment_id"));
        prescription.setDiagnosis(rs.getString("diagnosis"));
        prescription.setNotes(rs.getString("notes"));
        
        Timestamp prescribed = rs.getTimestamp("prescribed_at");
        if (prescribed != null) {
            prescription.setPrescribedAt(prescribed.toLocalDateTime());
        }
        
        return prescription;
    }
}
