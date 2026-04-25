package com.kasir.app.dao;

import com.kasir.app.config.KoneksiDatabase;
import com.kasir.app.model.Akun;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AkunDAO {
   // === BAGIAN LOGIN ===
    public Akun cekLogin(String user, String pass) {
        Akun akun = null;
        String sql = "SELECT * FROM akun WHERE username = ? AND PASSWORD = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, user);
                ps.setString(2, pass);
                ResultSet rs = ps.executeQuery();
                if(rs.next()) {
                    akun = new Akun(
                    rs.getInt("id_akun"),
                    rs.getString("username"),
                    rs.getString("password"), 
                    rs.getString("nama_lengkap"), 
                    rs.getString("role"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return akun;
    }

     public List<Akun> getAll() {
        List<Akun> list = new ArrayList<>();
        String sql = "SELECT * FROM akun";
        try (Connection conn = KoneksiDatabase.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Akun a = new Akun();
                a.setIdAkun(rs.getInt("id_akun"));
                a.setUsername(rs.getString("username"));
                a.setNamaLengkap(rs.getString("nama_lengkap"));
                a.setRole(rs.getString("role"));
                list.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // === TAMBAH AKUN BARU ===
    public boolean insert(Akun a) {
        String sql = "INSERT INTO akun (username, password, nama_lengkap, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = KoneksiDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getUsername());
            ps.setString(2, a.getPassword());
            ps.setString(3, a.getNamaLengkap());
            ps.setString(4, a.getRole());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace();
            return false; 
        }
    }

    // === GANTI PASSWORD ====
    public boolean update(Akun a) {
        // Query dinamis: kalau password kosong, jangan update password-nya
        boolean updatePass = a.getPassword() != null && !a.getPassword().isEmpty();
        String sql = updatePass 
            ? "UPDATE akun SET nama_lengkap = ?, role = ?, password = ? WHERE id_akun = ?"
            : "UPDATE akun SET nama_lengkap = ?, role = ? WHERE id_akun = ?";

        try (Connection conn = KoneksiDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, a.getNamaLengkap());
            ps.setString(2, a.getRole());
            
            if (updatePass) {
                ps.setString(3, a.getPassword());
                ps.setInt(4, a.getIdAkun());
            } else {
                ps.setInt(3, a.getIdAkun());
            }
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace();
            return false; 
        }
    }

    // === UNTUK HAPUS AKUN ====
    public boolean delete(int id) {
        String sql = "DELETE FROM akun WHERE id_akun = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace();
            return false; 
        }
    }
}
