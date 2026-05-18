package com.healthcare.dao;

import com.healthcare.model.Doctor;
import com.healthcare.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {
    
    public boolean createDoctor(Doctor doctor) throws SQLException {
        String sql = "INSERT INTO doctors (user_id, specialization, qualification, experience, consultation_fee, available_days, working_hours) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, doctor.getUserId());
            stmt.setString(2, doctor.getSpecialization());
            stmt.setString(3, doctor.getQualification());
            stmt.setInt(4, doctor.getExperience());
            stmt.setDouble(5, doctor.getConsultationFee());
            stmt.setString(6, doctor.getAvailableDays());
            stmt.setString(7, doctor.getWorkingHours());
            
            return stmt.executeUpdate() > 0;
        } finally {
            DatabaseUtil.close(conn, stmt, null);
        }
    }
    
    public Doctor getDoctorById(int id) throws SQLException {
        String sql = "SELECT d.*, u.name, u.email FROM doctors d JOIN users u ON d.user_id = u.id WHERE d.id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToDoctor(rs);
            }
            return null;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public Doctor getDoctorByUserId(int userId) throws SQLException {
        String sql = "SELECT d.*, u.name, u.email FROM doctors d JOIN users u ON d.user_id = u.id WHERE d.user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToDoctor(rs);
            }
            return null;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<Doctor> getAllDoctors() throws SQLException {
        String sql = "SELECT d.*, u.name, u.email FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.is_active = TRUE AND d.is_active = TRUE ORDER BY u.name";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Doctor> doctors = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                doctors.add(mapResultSetToDoctor(rs));
            }
            return doctors;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<Doctor> getActiveDoctors() throws SQLException {
        String sql = "SELECT d.*, u.name, u.email FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.is_active = TRUE AND d.is_active = TRUE ORDER BY d.specialization, u.name";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Doctor> doctors = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                doctors.add(mapResultSetToDoctor(rs));
            }
            return doctors;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public boolean updateDoctor(Doctor doctor) throws SQLException {
        String sql = "UPDATE doctors SET specialization = ?, qualification = ?, experience = ?, consultation_fee = ?, available_days = ?, working_hours = ?, is_active = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, doctor.getSpecialization());
            stmt.setString(2, doctor.getQualification());
            stmt.setInt(3, doctor.getExperience());
            stmt.setDouble(4, doctor.getConsultationFee());
            stmt.setString(5, doctor.getAvailableDays());
            stmt.setString(6, doctor.getWorkingHours());
            stmt.setBoolean(7, doctor.isActive());
            stmt.setInt(8, doctor.getId());
            
            return stmt.executeUpdate() > 0;
        } finally {
            DatabaseUtil.close(conn, stmt, null);
        }
    }
    
    // Soft delete – set is_active to FALSE
    public boolean deleteDoctor(int id) throws SQLException {
        String sql = "UPDATE doctors SET is_active = FALSE WHERE id = ?";
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
    
    public boolean toggleDoctorStatus(int doctorId) throws SQLException {
        String sql = "UPDATE doctors SET is_active = NOT is_active WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, doctorId);
            
            return stmt.executeUpdate() > 0;
        } finally {
            DatabaseUtil.close(conn, stmt, null);
        }
    }
    
    public List<Doctor> searchDoctors(String searchTerm) throws SQLException {
        String sql = "SELECT d.*, u.name, u.email FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.is_active = TRUE AND (u.name LIKE ? OR d.specialization LIKE ? OR u.email LIKE ?) ORDER BY d.specialization, u.name";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Doctor> doctors = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                doctors.add(mapResultSetToDoctor(rs));
            }
            return doctors;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public boolean isDoctorAvailable(int doctorId, String date, String time) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND date = ? AND time = ? AND status IN ('PENDING', 'CONFIRMED')";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, doctorId);
            stmt.setString(2, date);
            stmt.setString(3, time);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
            return false;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    private Doctor mapResultSetToDoctor(ResultSet rs) throws SQLException {
        Doctor doctor = new Doctor();
        doctor.setId(rs.getInt("id"));
        doctor.setUserId(rs.getInt("user_id"));
        doctor.setSpecialization(rs.getString("specialization"));
        doctor.setQualification(rs.getString("qualification"));
        doctor.setExperience(rs.getInt("experience"));
        doctor.setConsultationFee(rs.getDouble("consultation_fee"));
        doctor.setAvailableDays(rs.getString("available_days"));
        doctor.setWorkingHours(rs.getString("working_hours"));
        doctor.setActive(rs.getBoolean("is_active"));
        return doctor;
    }
}