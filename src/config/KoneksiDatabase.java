package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class KoneksiDatabase {
    private static final String URL = "jdbc:mysql://localhost:3306/pbo2_olahraga";
    private static final String USER = "root";
    private static final String PASS = "";

    // Method ini yang bakal dipanggil sama DAO
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // Method main ini tetep boleh ada buat ngetes doang
    public static void main(String[] args) {
        try {
            Connection conn = getConnection();
            if (conn != null) {
                System.out.println("GOKIL! Koneksi Berhasil, Bro!");
            }
        } catch (SQLException e) {
            System.out.println("Yah, gagal konek. Error: " + e.getMessage());
        }
    }
}