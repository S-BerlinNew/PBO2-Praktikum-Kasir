package dao;

import config.Koneksi;
import model.Barang;
import model.DetailPenjualan;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetailPenjualanDAO {
    // Ambil semua data detail penjualan
    public List<DetailPenjualan> getAll() {
        List<DetailPenjualan> listDetailPenjualan = new ArrayList<>();
        String sql = "SELECT * FROM detail_penjualan";
        
        try(Connection conn = Koneksi.getConnection();
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
                        rs.getString("no_nota"),
                        barangAsli,
                        rs.getInt("qty")
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
        String sql = "INSERT INTO detail_penjualan (no_nota, id_barang, qty, subtotal) VALUES(?, ?, ?, ?)";
        try(Connection conn = config.Koneksi.getConnection(); 
            PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, dp.getnoNota());
                ps.setString(2, dp.getBarang().getIdBarang());
                ps.setInt(3, dp.getQty());
                ps.setDouble(4, dp.getSubtotal());

                ps.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Eror insert Detail Penjualan " + e.getMessage());
            }
    }
}
