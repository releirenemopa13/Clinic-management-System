package com.healthcare.dao;

import com.healthcare.model.PrescriptionMedicine;
import com.healthcare.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionMedicineDAO {
    
    public boolean createPrescriptionMedicine(PrescriptionMedicine medicine) throws SQLException {
        String sql = "INSERT INTO prescription_medicines (prescription_id, medicine_name, dosage, duration, instructions) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, medicine.getPrescriptionId());
            stmt.setString(2, medicine.getMedicineName());
            stmt.setString(3, medicine.getDosage());
            stmt.setString(4, medicine.getDuration());
            stmt.setString(5, medicine.getInstructions());
            
            return stmt.executeUpdate() > 0;
        } finally {
            DatabaseUtil.close(conn, stmt, null);
        }
    }
    
    public PrescriptionMedicine getPrescriptionMedicineById(int id) throws SQLException {
        String sql = "SELECT * FROM prescription_medicines WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPrescriptionMedicine(rs);
            }
            return null;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public List<PrescriptionMedicine> getMedicinesByPrescriptionId(int prescriptionId) throws SQLException {
        String sql = "SELECT * FROM prescription_medicines WHERE prescription_id = ? ORDER BY id";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<PrescriptionMedicine> medicines = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, prescriptionId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                medicines.add(mapResultSetToPrescriptionMedicine(rs));
            }
            return medicines;
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
    }
    
    public boolean updatePrescriptionMedicine(PrescriptionMedicine medicine) throws SQLException {
        String sql = "UPDATE prescription_medicines SET medicine_name = ?, dosage = ?, duration = ?, instructions = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, medicine.getMedicineName());
            stmt.setString(2, medicine.getDosage());
            stmt.setString(3, medicine.getDuration());
            stmt.setString(4, medicine.getInstructions());
            stmt.setInt(5, medicine.getId());
            
            return stmt.executeUpdate() > 0;
        } finally {
            DatabaseUtil.close(conn, stmt, null);
        }
    }
    
    public boolean deletePrescriptionMedicine(int id) throws SQLException {
        String sql = "DELETE FROM prescription_medicines WHERE id = ?";
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
    
    public boolean deleteMedicinesByPrescriptionId(int prescriptionId) throws SQLException {
        String sql = "DELETE FROM prescription_medicines WHERE prescription_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, prescriptionId);
            
            return stmt.executeUpdate() > 0;
        } finally {
            DatabaseUtil.close(conn, stmt, null);
        }
    }
    
    private PrescriptionMedicine mapResultSetToPrescriptionMedicine(ResultSet rs) throws SQLException {
        PrescriptionMedicine medicine = new PrescriptionMedicine();
        medicine.setId(rs.getInt("id"));
        medicine.setPrescriptionId(rs.getInt("prescription_id"));
        medicine.setMedicineName(rs.getString("medicine_name"));
        medicine.setDosage(rs.getString("dosage"));
        medicine.setDuration(rs.getString("duration"));
        medicine.setInstructions(rs.getString("instructions"));
        
        return medicine;
    }
}
