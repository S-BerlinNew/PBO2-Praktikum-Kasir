package dao;

import config.KoneksiDatabase;
import model.Barang;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BarangDAO {
    // Ambil semua data barang
    public List<Barang> getAll() {
        List<Barang> listBarang = new ArrayList<>();
        String sql = "SELECT * FROM barang"; 
        //  KoneksiDatabase otomatis tertutup 
        try (Connection conn = KoneksiDatabase.getConnection();
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
                        rs.getInt("status")
                    );
                    listBarang.add(b); //datar antrian list
                }
                } catch (SQLException e) {
                    System.out.println("Penarikan data eror : " + e.getMessage());
                }
         return listBarang;
    }

    // Fungsi khusus untuk Kasir (Cuma yang status 1)
    public List<Barang> getAllAktif() {
        List<Barang> listBarang = new ArrayList<>();
        String sql = "SELECT * FROM barang WHERE status = 1"; 
        try (Connection conn = KoneksiDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Barang b = new Barang(
                    rs.getString("id_barang"),    
                    rs.getString("nama_barang"),
                    rs.getDouble("harga_jual"),
                    rs.getDouble("harga_modal"),
                    rs.getString("jenis_barang"),
                    rs.getString("brand"),
                    rs.getString("warna"),
                    rs.getInt("stok"),
                    rs.getInt("status")
                );
                listBarang.add(b);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return listBarang;
    }

    // Pengambilan satu barang untu detail penjualan
    public Barang getById(String id) {
        Barang b = null;

        String sql = "SELECT * FROM barang WHERE id_barang = ?";

        try (Connection conn = KoneksiDatabase.getConnection();
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
                            rs.getInt("status")
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
        try (Connection conn = KoneksiDatabase.getConnection();
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
    String sql = "INSERT INTO barang (id_barang, nama_barang, harga_jual, harga_modal," + //
                "                 jenis_barang, brand, warna, stok, diskon, status)" + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = config.KoneksiDatabase.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, b.getIdBarang());
            ps.setString(2, b.getNamaBarang());
            ps.setDouble(3, b.getHargaJual());
            ps.setDouble(4, b.getHargaModal());
            ps.setString(5, b.getJenisBarang());
            ps.setString(6, b.getBrand());
            ps.setString(7, b.getWarna());
            ps.setInt(8, b.getStok());
            ps.setInt(9, 0);
            ps.setInt(10, b.getStatus());
            
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error Insert Barang : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // UNTUK UPDATE DATA BARANG
    public void update(Barang b) {
        String sql = "UPDATE barang SET nama_barang=?, harga_jual=?, harga_modal=?," +
                    "jenis_barang=?, brand=?, warna=?, stok=?, diskon=? WHERE id_barang=?";
        
        try (Connection conn = KoneksiDatabase.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getNamaBarang());
            ps.setDouble(2, b.getHargaJual());
            ps.setDouble(3, b.getHargaModal());
            ps.setString(4, b.getJenisBarang());
            ps.setString(5, b.getBrand());
            ps.setString(6, b.getWarna());
            ps.setInt(7, b.getStok());
            ps.setInt(8, 0);
            ps.setString(9, b.getIdBarang());

            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error Update Barang: " + e.getMessage());
        }
    }

    //UNTUK UPDATE INFO STATUS AKTIF DAN NONAKTIF BARANG
    public void updateStatus(String id, int status) {
        String sql = "UPDATE barang SET status=? WHERE id_barang=?";
        try (Connection conn = KoneksiDatabase.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, status);   // parameter ke-1 adalah status
            ps.setString(2, id);    // parameter ke-2 adalah id_barang
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error Update Status: " + e.getMessage());
        }
    }
}
