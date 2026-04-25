package com.kasir.app.dao;

import com.kasir.app.config.KoneksiDatabase;
import com.kasir.app.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class CustomerDAO {
    // Ambil semua data customer
    public List<Customer> getAll() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customer";

        try (Connection conn = KoneksiDatabase.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Customer c = new Customer(
                        rs.getInt("id_customer"),
                        rs.getString("kode_customer"),
                        rs.getString("nama_customer"),
                        rs.getString("no_telp")
                    );
                    list.add(c);
                }
            } catch (SQLException e) {
                System.out.println("Penarikan data eror : " + e.getMessage());
            }
            return list;
    }

    // Insert ke database
    public void insert(Customer c) {
        String sql = "INSERT INTO customer (kode_customer, nama_customer, no_telp) VALUES (?, ?, ?)";
        try(Connection conn = KoneksiDatabase.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, c.getKodeCustomer());
                ps.setString(2, c.getNamaCustomer());
                ps.setString(3, c.getNoTelp());
                ps.executeUpdate();

                System.out.println("Data Customer Berhasil Disimpan ke DB!");
            } catch (SQLException e) {
                System.out.println("Gagal Simpan Customer: " + e.getMessage());
                // Lempar eror supaya Form yang tangkap
                throw new RuntimeException(e);
            }
    }

    // 1. Method untuk cari customer berdasarkan nama (Biar gak error getByName)
    public Customer getByName(String nama) {
        Customer cust = null;
        String sql = "SELECT * FROM customer WHERE nama_customer = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nama);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Di sini butuh KONSTRUKTOR KOSONG di model Customer!
                cust = new Customer(); 
                cust.setIdCustomer(rs.getInt("id_customer"));
                cust.setNamaCustomer(rs.getString("nama_customer"));
                cust.setKodeCustomer(rs.getString("kode_customer"));
                cust.setNoTelp(rs.getString("no_telp"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cust;
    }

    //Mehtod membuat kode customer
    public String generateKodeBaru() {
        String kode = "CUS001"; // Default
        String sql = "SELECT kode_customer FROM customer ORDER BY id_customer DESC LIMIT 1";
        
        try (Connection conn = KoneksiDatabase.getConnection(); // Gue sederhanain panggilannya
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                String kodeLama = rs.getString("kode_customer"); 
                // Pastikan kodeLama minimal ada 4 karakter (CUS + 1 angka)
                if (kodeLama != null && kodeLama.length() >= 4) {
                    try {
                        // Ambil angka dari index ke-3 sampai habis
                        int angka = Integer.parseInt(kodeLama.substring(3)) + 1;
                        kode = String.format("CUS%03d", angka); 
                    } catch (NumberFormatException e) {
                        // Kalau ternyata kodenya aneh (bukan angka), balik ke default
                        kode = "CUS001";
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error Generate Kode: " + e.getMessage());
        }
        return kode;
    }

    //Method untuk update/edit 
    public void update(Customer c) {
        String sql = "UPDATE customer SET nama_customer=?, no_telp=? WHERE id_customer=?";
        try (Connection conn = KoneksiDatabase.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, c.getNamaCustomer());
            ps.setString(2, c.getNoTelp());
            ps.setInt(3, c.getIdCustomer());
            
            ps.executeUpdate();
            System.out.println("Update Berhasil!");
        } catch (SQLException e) {
            System.out.println("Update Gagal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Method untuk delete 
    public void delete(int id) {
        String sql = "DELETE FROM customer WHERE id_customer=?";
        try (Connection conn = KoneksiDatabase.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Data Berhasil Dihapus!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2. Method untuk simpan customer baru & ambil ID-nya
    public int insertAndGetId(Customer c) {
    String sql = "INSERT INTO customer (kode_customer, nama_customer, no_telp) VALUES (?, ?, ?)";
        try (Connection conn = KoneksiDatabase.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, c.getKodeCustomer());
            ps.setString(2, c.getNamaCustomer());
            ps.setString(3, c.getNoTelp());
            
            System.out.println("Sedang mencoba insert customer: " + c.getNamaCustomer());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                System.out.println("BERHASIL! ID Customer Baru: " + id);
                return id;
            } else {
                System.out.println("Gagal: Database tidak memberikan ID baru.");
            }
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Error di CustomerDAO: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}
