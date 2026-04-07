package dao;

import config.Koneksi;
import model.Barang;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BarangDAO {
    // Ambil semua data barang
    public List<Barang> getAll() {
        List<Barang> listBarang = new ArrayList<>();
        String sql = "SELECT * FROM barang";

        // Pakai try-with-resources biar koneksi otomatis tertutup (Anti-Bocor!)
        try (Connection conn = Koneksi.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    // Proses bungkus data dari baris Tabel ke Objek Java
                    Barang b = new Barang(
                        rs.getString("id_barang"),    
                        rs.getString("nama_barang"),
                        rs.getDouble("harga_jual"),
                        rs.getDouble("harga_modal"),
                        rs.getString("jenis_barang"),
                        rs.getString("brand"),
                        rs.getString("warna"),
                        rs.getInt("stok"),
                        rs.getDouble("diskon")
                    );
                    listBarang.add(b); //datar antrian list
                }
                } catch (SQLException e) {
                    System.out.println("Penarikan data eror : " + e.getMessage());
                }
         return listBarang;
    }

    // Pengambilan satu barang untu detail penjualan
    public Barang getById(String id) {
        Barang b = null;

        String sql = "SELECT * FROM barang WHERE id_barang = ?";

        try (Connection conn = Koneksi.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, id); // untuk mengisi ? di sql

                try(ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        b = new Barang(
                            rs.getString("id_barang"),    
                            rs.getString("nama_barang"),
                            rs.getDouble("harga_jual"),
                            rs.getDouble("harga_modal"),
                            rs.getString("jenis_barang"),
                            rs.getString("brand"),
                            rs.getString("warna"),
                            rs.getInt("stok"),
                            rs.getDouble("diskon")
                        );
                    }
                }
            } catch (SQLException e) {
                System.out.println("Gagal mencari barang by ID : " + e.getMessage());
            }
            return b;
    } 

    // Pengurangan Stok(qty) jika barang kejual
    public void kurangiStok(String id, int jumlah) {
        String sql = "UPDATE barang SET stok = stok - ? WHERE id_barang = ?";
        try (Connection conn = Koneksi.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jumlah);
            ps.setString(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Insert ke database
    public void insert(Barang b) {
    String sql = "INSERT INTO barang VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = config.Koneksi.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, b.getIdBarang());
            ps.setString(2, b.getNamaBarang());
            ps.setDouble(3, b.getHargaJual());
            ps.setDouble(4, b.getHargaModal());
            ps.setString(5, b.getJenisBarang());
            ps.setString(6, b.getBrand());
            ps.setString(7, b.getWarna());
            ps.setInt(8, b.getStok());
            ps.setDouble(9, b.getDiskon());
            
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error Insert Barang : " + e.getMessage());
        }
    }
}
