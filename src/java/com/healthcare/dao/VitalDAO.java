package com.healthcare.dao;

import com.healthcare.model.Vital;
import com.healthcare.util.DatabaseUtil;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VitalDAO {
    
    public boolean createVital(Vital vital) throws SQLException {
        String sql = "INSERT INTO vitals (patient_id, bp_systolic, bp_diastolic, heart_rate, blood_sugar, weight, temperature) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, vital.getPatientId());
            stmt.setInt(2, vital.getBpSystolic());
            stmt.setInt(3, vital.getBpDiastolic());
            stmt.setInt(4, vital.getHeartRate());
            stmt.setDouble(5, vital.getBloodSugar());
            stmt.setDouble(6, vital.getWeight());
            stmt.setDouble(7, vital.getTemperature());
            
            return stmt.executeUpdate() > 0;
        } finally {
            DatabaseUtil.close(conn, stmt, null);
        }
    }
    
    public Vital getVitalById(int id) throws SQLException {
        String sql = "SELECT * FROM vitals WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToVital(rs);
            }
            return null;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<Vital> getVitalsByPatientId(int patientId) throws SQLException {
        String sql = "SELECT * FROM vitals WHERE patient_id = ? ORDER BY recorded_at DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Vital> vitals = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                vitals.add(mapResultSetToVital(rs));
            }
            return vitals;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<Vital> getRecentVitals(int patientId, int limit) throws SQLException {
        String sql = "SELECT * FROM vitals WHERE patient_id = ? ORDER BY recorded_at DESC LIMIT ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Vital> vitals = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);
            stmt.setInt(2, limit);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                vitals.add(mapResultSetToVital(rs));
            }
            return vitals;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<Vital> getVitalsByDateRange(int patientId, String startDate, String endDate) throws SQLException {
        String sql = "SELECT * FROM vitals WHERE patient_id = ? AND DATE(recorded_at) BETWEEN ? AND ? ORDER BY recorded_at DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Vital> vitals = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);
            stmt.setString(2, startDate);
            stmt.setString(3, endDate);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                vitals.add(mapResultSetToVital(rs));
            }
            return vitals;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<Vital> getVitalsByDays(int patientId, int days) throws SQLException {
        String sql = "SELECT * FROM vitals WHERE patient_id = ? AND recorded_at >= DATE_SUB(NOW(), INTERVAL ? DAY) ORDER BY recorded_at DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Vital> vitals = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);
            stmt.setInt(2, days);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                vitals.add(mapResultSetToVital(rs));
            }
            return vitals;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public boolean updateVital(Vital vital) throws SQLException {
        String sql = "UPDATE vitals SET bp_systolic = ?, bp_diastolic = ?, heart_rate = ?, blood_sugar = ?, weight = ?, temperature = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, vital.getBpSystolic());
            stmt.setInt(2, vital.getBpDiastolic());
            stmt.setInt(3, vital.getHeartRate());
            stmt.setDouble(4, vital.getBloodSugar());
            stmt.setDouble(5, vital.getWeight());
            stmt.setDouble(6, vital.getTemperature());
            stmt.setInt(7, vital.getId());
            
            return stmt.executeUpdate() > 0;
        } finally {
            DatabaseUtil.close(conn, stmt, null);
        }
    }
    
    public boolean deleteVital(int id) throws SQLException {
        String sql = "DELETE FROM vitals WHERE id = ?";
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
    
    public List<Vital> getVitalsForChart(int patientId, String metric, int days) throws SQLException {
        String sql;
        switch (metric.toLowerCase()) {
            case "bp":
                sql = "SELECT DATE(recorded_at) as date, AVG(bp_systolic) as bp_systolic, AVG(bp_diastolic) as bp_diastolic FROM vitals WHERE patient_id = ? AND recorded_at >= DATE_SUB(NOW(), INTERVAL ? DAY) GROUP BY DATE(recorded_at) ORDER BY date";
                break;
            case "heart_rate":
                sql = "SELECT DATE(recorded_at) as date, AVG(heart_rate) as heart_rate FROM vitals WHERE patient_id = ? AND recorded_at >= DATE_SUB(NOW(), INTERVAL ? DAY) GROUP BY DATE(recorded_at) ORDER BY date";
                break;
            case "blood_sugar":
                sql = "SELECT DATE(recorded_at) as date, AVG(blood_sugar) as blood_sugar FROM vitals WHERE patient_id = ? AND recorded_at >= DATE_SUB(NOW(), INTERVAL ? DAY) GROUP BY DATE(recorded_at) ORDER BY date";
                break;
            case "weight":
                sql = "SELECT DATE(recorded_at) as date, AVG(weight) as weight FROM vitals WHERE patient_id = ? AND recorded_at >= DATE_SUB(NOW(), INTERVAL ? DAY) GROUP BY DATE(recorded_at) ORDER BY date";
                break;
            default:
                sql = "SELECT * FROM vitals WHERE patient_id = ? AND recorded_at >= DATE_SUB(NOW(), INTERVAL ? DAY) ORDER BY recorded_at DESC";
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Vital> vitals = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);
            stmt.setInt(2, days);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                vitals.add(mapResultSetToVital(rs));
            }
            return vitals;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    private Vital mapResultSetToVital(ResultSet rs) throws SQLException {
        Vital vital = new Vital();
        vital.setId(rs.getInt("id"));
        vital.setPatientId(rs.getInt("patient_id"));
        vital.setBpSystolic(rs.getInt("bp_systolic"));
        vital.setBpDiastolic(rs.getInt("bp_diastolic"));
        vital.setHeartRate(rs.getInt("heart_rate"));
        vital.setBloodSugar(rs.getDouble("blood_sugar"));
        vital.setWeight(rs.getDouble("weight"));
        vital.setTemperature(rs.getDouble("temperature"));
        
        Timestamp timestamp = rs.getTimestamp("recorded_at");
        if (timestamp != null) {
            vital.setRecordedAt(timestamp.toLocalDateTime());
        }
        
        return vital;
    }
}
