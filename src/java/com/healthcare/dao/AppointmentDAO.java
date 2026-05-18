package com.healthcare.dao;

import com.healthcare.model.Appointment;
import com.healthcare.util.DatabaseUtil;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    
    public boolean createAppointment(Appointment appointment) throws SQLException {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, date, time, symptoms, status) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, appointment.getPatientId());
            stmt.setInt(2, appointment.getDoctorId());
            stmt.setString(3, appointment.getDate());
            stmt.setString(4, appointment.getTime());
            stmt.setString(5, appointment.getSymptoms());
            stmt.setString(6, appointment.getStatus());
            
            return stmt.executeUpdate() > 0;
        } finally {
            DatabaseUtil.close(conn, stmt, null);
        }
    }
    
    public Appointment getAppointmentById(int id) throws SQLException {
        String sql = "SELECT a.*, p.name as patient_name, d.name as doctor_name, specialization FROM appointments a " +
                    "JOIN patients pat ON a.patient_id = pat.id " +
                    "JOIN users p ON pat.user_id = p.id " +
                    "JOIN doctors doc ON a.doctor_id = doc.id " +
                    "JOIN users d ON doc.user_id = d.id " +
                    "WHERE a.id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToAppointment(rs);
            }
            return null;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<Appointment> getAppointmentsByPatientId(int patientId) throws SQLException {
        String sql = "SELECT a.*, p.name as patient_name, d.name as doctor_name, specialization FROM appointments a " +
                    "JOIN patients pat ON a.patient_id = pat.id " +
                    "JOIN users p ON pat.user_id = p.id " +
                    "JOIN doctors doc ON a.doctor_id = doc.id " +
                    "JOIN users d ON doc.user_id = d.id " +
                    "WHERE a.patient_id = ? ORDER BY a.date DESC, a.time DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Appointment> appointments = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
            return appointments;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<Appointment> getAppointmentsByDoctorId(int doctorId) throws SQLException {
        String sql = "SELECT a.*, p.name as patient_name, d.name as doctor_name, specialization FROM appointments a " +
                    "JOIN patients pat ON a.patient_id = pat.id " +
                    "JOIN users p ON pat.user_id = p.id " +
                    "JOIN doctors doc ON a.doctor_id = doc.id " +
                    "JOIN users d ON doc.user_id = d.id " +
                    "WHERE a.doctor_id = ? ORDER BY a.date DESC, a.time DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Appointment> appointments = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, doctorId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
            return appointments;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<Appointment> getTodayAppointmentsByDoctor(int doctorId) throws SQLException {
        if (doctorId == 0) return new ArrayList<>();
        String sql = "SELECT a.*, p.name as patient_name, d.name as doctor_name, specialization FROM appointments a " +
                    "JOIN patients pat ON a.patient_id = pat.id " +
                    "JOIN users p ON pat.user_id = p.id " +
                    "JOIN doctors doc ON a.doctor_id = doc.id " +
                    "JOIN users d ON doc.user_id = d.id " +
                    "WHERE a.doctor_id = ? AND a.date = CURDATE() ORDER BY a.time";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Appointment> appointments = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, doctorId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
            return appointments;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<Appointment> getUpcomingAppointmentsByPatient(int patientId, int limit) throws SQLException {
        String sql = "SELECT a.*, p.name as patient_name, d.name as doctor_name, specialization FROM appointments a " +
                    "JOIN patients pat ON a.patient_id = pat.id " +
                    "JOIN users p ON pat.user_id = p.id " +
                    "JOIN doctors doc ON a.doctor_id = doc.id " +
                    "JOIN users d ON doc.user_id = d.id " +
                    "WHERE a.patient_id = ? AND a.date >= CURDATE() AND a.status IN ('PENDING', 'CONFIRMED') " +
                    "ORDER BY a.date, a.time LIMIT ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Appointment> appointments = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);
            stmt.setInt(2, limit);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
            return appointments;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<Appointment> getAppointmentsByStatus(String status) throws SQLException {
        String sql = "SELECT a.*, p.name as patient_name, d.name as doctor_name, specialization FROM appointments a " +
                    "JOIN patients pat ON a.patient_id = pat.id " +
                    "JOIN users p ON pat.user_id = p.id " +
                    "JOIN doctors doc ON a.doctor_id = doc.id " +
                    "JOIN users d ON doc.user_id = d.id " +
                    "WHERE a.status = ? ORDER BY a.date DESC, a.time DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Appointment> appointments = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
            return appointments;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<Appointment> getAppointmentsByDateRange(String startDate, String endDate) throws SQLException {
        String sql = "SELECT a.*, p.name as patient_name, d.name as doctor_name, specialization FROM appointments a " +
                    "JOIN patients pat ON a.patient_id = pat.id " +
                    "JOIN users p ON pat.user_id = p.id " +
                    "JOIN doctors doc ON a.doctor_id = doc.id " +
                    "JOIN users d ON doc.user_id = d.id " +
                    "WHERE a.date BETWEEN ? AND ? ORDER BY a.date DESC, a.time DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Appointment> appointments = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
            return appointments;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public boolean updateAppointment(Appointment appointment) throws SQLException {
        String sql = "UPDATE appointments SET date = ?, time = ?, symptoms = ?, status = ?, consultation_notes = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, appointment.getDate());
            stmt.setString(2, appointment.getTime());
            stmt.setString(3, appointment.getSymptoms());
            stmt.setString(4, appointment.getStatus());
            stmt.setString(5, appointment.getConsultationNotes());
            stmt.setInt(6, appointment.getId());
            
            return stmt.executeUpdate() > 0;
        } finally {
            DatabaseUtil.close(conn, stmt, null);
        }
    }
    
    public boolean updateAppointmentStatus(int appointmentId, String status) throws SQLException {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, appointmentId);
            
            return stmt.executeUpdate() > 0;
        } finally {
            DatabaseUtil.close(conn, stmt, null);
        }
    }
    
    public boolean cancelAppointment(int appointmentId) throws SQLException {
        String sql = "UPDATE appointments SET status = 'CANCELLED' WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, appointmentId);
            
            return stmt.executeUpdate() > 0;
        } finally {
            DatabaseUtil.close(conn, stmt, null);
        }
    }
    
    public boolean deleteAppointment(int id) throws SQLException {
        String sql = "DELETE FROM appointments WHERE id = ?";
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
    
    public int getPendingAppointmentsCount(int doctorId) throws SQLException {
        if (doctorId == 0) return 0;
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND status = 'PENDING'";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, doctorId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<String> getAvailableTimeSlots(int doctorId, String date) throws SQLException {
        List<String> allSlots = generateTimeSlots("09:00", "17:00", 30);
        List<String> bookedSlots = getBookedTimeSlots(doctorId, date);
        
        allSlots.removeAll(bookedSlots);
        return allSlots;
    }
    
    private List<String> getBookedTimeSlots(int doctorId, String date) throws SQLException {
        String sql = "SELECT time FROM appointments WHERE doctor_id = ? AND date = ? AND status IN ('PENDING', 'CONFIRMED')";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<String> bookedSlots = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, doctorId);
            stmt.setString(2, date);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookedSlots.add(rs.getString("time"));
            }
            return bookedSlots;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    private List<String> generateTimeSlots(String startTime, String endTime, int intervalMinutes) {
        List<String> slots = new ArrayList<>();
        String[] start = startTime.split(":");
        String[] end = endTime.split(":");
        
        int startHour = Integer.parseInt(start[0]);
        int startMin = Integer.parseInt(start[1]);
        int endHour = Integer.parseInt(end[0]);
        int endMin = Integer.parseInt(end[1]);
        
        int currentHour = startHour;
        int currentMin = startMin;
        
        while (currentHour < endHour || (currentHour == endHour && currentMin < endMin)) {
            slots.add(String.format("%02d:%02d", currentHour, currentMin));
            currentMin += intervalMinutes;
            if (currentMin >= 60) {
                currentMin -= 60;
                currentHour++;
            }
        }
        
        return slots;
    }
    
    public List<Appointment> getAllAppointments() throws SQLException {
        String sql = "SELECT a.*, p.name as patient_name, d.name as doctor_name, specialization FROM appointments a " +
                    "JOIN patients pat ON a.patient_id = pat.id " +
                    "JOIN users p ON pat.user_id = p.id " +
                    "JOIN doctors doc ON a.doctor_id = doc.id " +
                    "JOIN users d ON doc.user_id = d.id " +
                    "ORDER BY a.date DESC, a.time DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Appointment> appointments = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
            return appointments;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public int getTotalAppointmentsCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments";
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
    
    public int getTodayAppointmentsCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE date = CURDATE()";
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
    
    public int getUniquePatientsCountByDoctor(int doctorId) throws SQLException {
        if (doctorId == 0) return 0;
        
        String sql = "SELECT COUNT(DISTINCT patient_id) FROM appointments WHERE doctor_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, doctorId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public int getThisWeekAppointmentsCountByDoctor(int doctorId) throws SQLException {
        if (doctorId == 0) return 0;
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND YEARWEEK(date) = YEARWEEK(CURDATE())";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, doctorId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public int getCompletedTodayCountByDoctor(int doctorId) throws SQLException {
        if (doctorId == 0) return 0;
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND date = CURDATE() AND status = 'COMPLETED'";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, doctorId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setId(rs.getInt("id"));
        appointment.setPatientId(rs.getInt("patient_id"));
        appointment.setDoctorId(rs.getInt("doctor_id"));
        appointment.setDate(rs.getString("date"));
        appointment.setTime(rs.getString("time"));
        appointment.setSymptoms(rs.getString("symptoms"));
        appointment.setStatus(rs.getString("status"));
        appointment.setConsultationNotes(rs.getString("consultation_notes"));
        
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) {
            appointment.setCreatedAt(created.toLocalDateTime());
        }
        
        return appointment;
    }
}
