package dao;

import config.KoneksiDatabase;
import model.Akun;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AkunDAO {
    // untuk login
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

    // untuk tambah akun baru
    public boolean insert(Akun a) {
        String sql = "INSERT INTO akun (username, password, nama_lengkap) VALUES (?, ?, ?)";
        try (Connection conn = KoneksiDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getUsername());
            ps.setString(2, a.getPassword());
            ps.setString(3, "Admin Baru");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // Untuk ganti password dan username
    public boolean update(Akun a) {
        String sql = "UPDATE akun SET username = ?, password = ? WHERE id_akun = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getUsername());
            ps.setString(2, a.getPassword());
            ps.setInt(3, a.getIdAkun());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
}
