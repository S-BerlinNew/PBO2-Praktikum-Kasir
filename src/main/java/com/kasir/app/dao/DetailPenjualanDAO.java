package com.kasir.app.dao;

import com.kasir.app.config.KoneksiDatabase;
import com.kasir.app.model.Barang;
import com.kasir.app.model.DetailPenjualan;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetailPenjualanDAO {
    // Ambil semua data detail penjualan
    public List<DetailPenjualan> getAll() {
        List<DetailPenjualan> listDetailPenjualan = new ArrayList<>();
        String sql = "SELECT * FROM detail_penjualan";
        
        try(Connection conn = KoneksiDatabase.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
                
                while (rs.next()) {
                    // Ambil id barang dari detail_penjualan
                    String idBarang = rs.getString("id_barang");
                    // Memanggil BarangDAO untuk mencari data lengkap barang
                    BarangDAO bDAO = new BarangDAO();
                    Barang barangAsli = bDAO.getById(idBarang);

                    DetailPenjualan ds = new DetailPenjualan(
                        rs.getInt("id_detail"),
                        rs.getInt("id_penjualan"),
                        barangAsli,
                        rs.getInt("qty"),
                        rs.getDouble("subtotal")
                    );
                    listDetailPenjualan.add(ds);
                }
            } catch (SQLException e) {
                System.out.println("Penarikan data eror : " + e.getMessage());
            }
            return listDetailPenjualan;
    }

    // Insert ke database
    public void insert(DetailPenjualan dp) {
        String sql = "INSERT INTO detail_penjualan (id_penjualan, id_barang, qty, subtotal) VALUES(?, ?, ?, ?)";
        try(Connection conn = KoneksiDatabase.getConnection(); 
            PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, dp.getIdPenjualan());
                ps.setString(2, dp.getBarang().getIdBarang());
                ps.setInt(3, dp.getQty());
                ps.setDouble(4, dp.getSubtotal());

                ps.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Eror insert Detail Penjualan " + e.getMessage());
                e.printStackTrace();
            }
    }
}
