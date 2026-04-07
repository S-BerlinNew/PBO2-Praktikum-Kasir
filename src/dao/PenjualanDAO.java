package dao;

import config.Koneksi;
import model.Customer;
import model.Penjualan;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PenjualanDAO {
    // Ambil semua data penjualan
    public List<Penjualan> getAll() {
        List<Penjualan> listPenjualan = new ArrayList<>();
        String sql = "SELECT * FROM penjualan";

        try (Connection conn = Koneksi.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Customer cDummy = new Customer(rs.getInt("id_customer"), "", "", "");

                    Penjualan p = new Penjualan(
                        rs.getString("no_nota"),
                        rs.getDate("tanggal"),
                        cDummy,
                        rs.getString("nama_kasir"),
                        rs.getString("metode_pemabayarn"),
                        null
                    );
                    listPenjualan.add(p);
                }
            } catch (SQLException e) {
                System.out.println("Penarikan data eror : " + e.getMessage());
            }
            return listPenjualan;
    } 

    // Insert ke Database
    public void insert(Penjualan p) {
        String sql = "INSERT INTO penjualan (no_nota, tanggal, id_customer, nama_kasir, metode_pembayaran, diskon, total_bayar)" + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = config.Koneksi.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setString(1, p.getNoNota());
                ps.setDate(2, new java.sql.Date(p.getTanggal().getTime()));
                ps.setInt(3, p.getCustomer().getIdCustomer());
                ps.setString(4, p.getNamaKasir());
                ps.setString(5, p.getMetodePembayaran());
                ps.setDouble(6, p.getDiskon());
                ps.setDouble(7, p.getTotalBayar());

                ps.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Eror insert Penjualan : " + e.getMessage());
            }
    }
}
