package com.kasir.app.dao;

import com.kasir.app.config.KoneksiDatabase;
import com.kasir.app.model.Customer;
import com.kasir.app.model.Penjualan;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PenjualanDAO {
    // AMBIL DATA PENJUALAN
    public List<Penjualan> getAll() {
        List<Penjualan> listPenjualan = new ArrayList<>();
        String sql = "SELECT * FROM penjualan";

        try (Connection conn = KoneksiDatabase.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Customer cDummy = new Customer(rs.getInt("id_customer"), "", "", "");

                    Penjualan p = new Penjualan(
                        rs.getInt("id_penjualan"),
                        rs.getString("no_nota"),
                        rs.getDate("tanggal"),
                        cDummy,
                        rs.getString("nama_kasir"),
                        rs.getString("metode_pembayaran"),
                        rs.getInt("diskon"),
                        new ArrayList<>()
                    );
                    listPenjualan.add(p);
                }
            } catch (SQLException e) {
                System.out.println("Penarikan data eror : " + e.getMessage());
            }
            return listPenjualan;
    } 

    // === INSERT KE DB
    public int InsertAndGetId(Penjualan p) {
        String sql = "INSERT INTO penjualan (no_nota, tanggal, id_customer, nama_kasir, metode_pembayaran, diskon, total_bayar) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int generatedId = 0;
        
        try (Connection conn = KoneksiDatabase.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                
                ps.setString(1, p.getNoNota());
                ps.setDate(2, new java.sql.Date(p.getTanggal().getTime()));
                ps.setInt(3, p.getCustomer().getIdCustomer());
                ps.setString(4, p.getNamaKasir());
                ps.setString(5, p.getMetodePembayaran());
                ps.setInt(6, p.getDiskon());
                ps.setDouble(7, p.getTotalBayar());

                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1); // Ambil angka ID dari kolom pertama
                    }
        }
            } catch (SQLException e) {
                System.out.println("Eror insert Penjualan : " + e.getMessage());
                e.printStackTrace();
            }
            return generatedId;
    }
}
