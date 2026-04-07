package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

import dao.BarangDAO;
import model.Barang;

import java.awt.*;

public class FormBarang extends JFrame{

    // Komponen
    private JTextField txtId, txtNama, txtHargaJual, txtHargaModal, txtJenis, txtBrand, txtWarna, txtStok, txtDiskon;
    private JTable tabelBarang;
    private DefaultTableModel modelTabel;
    private BarangDAO bDAO = new BarangDAO();

    public FormBarang() {
        //Judul Jendela
        setTitle("Kelola Data Barang - Toko Olahraga");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel Input 
        JPanel panelInput = new JPanel(new GridLayout(3, 6, 10, 10));
        panelInput.setBorder(BorderFactory.createTitledBorder("Input Data Barang"));

        panelInput.add(new JLabel("ID Barang:"));
        txtId = new JTextField();
        panelInput.add(txtId);

        panelInput.add(new JLabel("Nama Barang:"));
        txtNama = new JTextField();
        panelInput.add(txtNama);

        panelInput.add(new JLabel("Harga Jual:"));
        txtHargaJual = new JTextField();
        panelInput.add(txtHargaJual);

        panelInput.add(new JLabel("Harga Modal:"));
        txtHargaModal = new JTextField();
        panelInput.add(txtHargaModal);

        panelInput.add(new JLabel("Jenis:"));
        txtJenis = new JTextField();
        panelInput.add(txtJenis);

        panelInput.add(new JLabel("Brand:"));
        txtBrand = new JTextField();
        panelInput.add(txtBrand);

        panelInput.add(new JLabel("Warna:"));
        txtWarna = new JTextField();
        panelInput.add(txtWarna);

        panelInput.add(new JLabel("Stok:"));
        txtStok = new JTextField();
        panelInput.add(txtStok);

        panelInput.add(new JLabel("Diskon (%):")); // <--- Bintang Tamu Kita
        txtDiskon = new JTextField("0");
        panelInput.add(txtDiskon);

        add(panelInput, BorderLayout.NORTH);

        // Panel Tabel Tengah
        String[] kolom = {"ID", "Nama", "Harga Jual", "Harga Modal", "Jenis", "Brand", "Warna", "Stok", "Diskon"};
        modelTabel = new DefaultTableModel(kolom, 0);
        tabelBarang = new JTable(modelTabel);
        add(new JScrollPane(tabelBarang), BorderLayout.CENTER);

        // Panel Tombol
        JPanel panelTombol = new JPanel();
        JButton btnSimpan = new JButton("SIMPAN BARU");
        JButton btnRefresh = new JButton("REFRESH DATA");
        
        panelTombol.add(btnSimpan);
        panelTombol.add(btnRefresh);
        add(panelTombol, BorderLayout.SOUTH);

        // Load data pertama kali
        loadData();

        // Event Tombol Refresh
        btnRefresh.addActionListener(e -> loadData());

        // Event Tombol Simpan Baru
    btnSimpan.addActionListener(e -> {
        try {
            // 1. Ambil data dari TextField (Sesuai nama variabel lo)
            String id = txtId.getText();
            String nama = txtNama.getText();
            double jual = Double.parseDouble(txtHargaJual.getText());
            double modal = Double.parseDouble(txtHargaModal.getText());
            String jenis = txtJenis.getText();
            String brand = txtBrand.getText();
            String warna = txtWarna.getText();
            int stok = Integer.parseInt(txtStok.getText());
            double diskon = Double.parseDouble(txtDiskon.getText());

            // 2. Bungkus ke Objek Barang (Object Collaboration)
            Barang bBaru = new Barang(id, nama, jual, modal, jenis, brand, warna, stok, diskon);

            // 3. Panggil DAO buat simpan ke MySQL (Persistence)
            bDAO.insert(bBaru);

            // 4. Kasih tau user & Refresh tabel
            JOptionPane.showMessageDialog(this, "Barang " + nama + " Berhasil Disimpan!");
            loadData(); // Biar langsung muncul di tabel bawahnya
            
            // 5. Bersihin inputan biar bisa ngetik barang baru lagi
            txtId.setText("");
            txtNama.setText("");
            txtHargaJual.setText("");
            txtHargaModal.setText("");
            txtStok.setText("");
            // ... dst (optional)

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Harga, Stok, dan Diskon harus berupa angka!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal simpan: " + ex.getMessage());
            }
        });
    }

    private void loadData() {
        modelTabel.setRowCount(0); // Kosongin tabel dulu
        List<Barang> list = bDAO.getAll();
        for (Barang b : list) {
            modelTabel.addRow(new Object[]{
                b.getIdBarang(), b.getNamaBarang(), b.getHargaJual(),
                b.getHargaModal(), b.getJenisBarang(), b.getBrand(),
                b.getWarna(), b.getStok(), b.getDiskon() + "%"
            });
        }
    }


        
    public static void main(String[] args) {
        // Cara jalanin GUI biar stabil
        SwingUtilities.invokeLater(() -> {
            new FormBarang().setVisible(true);
        });
    }
}
