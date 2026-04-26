package com.kasir.app.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.kasir.app.config.KoneksiDatabase;
import com.kasir.app.dao.BarangDAO;
import com.kasir.app.model.Barang;
import com.kasir.app.model.UserSession;


public class FormBarang extends JFrame{
    private JTextField txtId, txtNama, txtHargaJual, txtHargaModal, txtJenis, txtBrand, txtWarna, txtStok;
    private JTable tabelBarang;
    private DefaultTableModel modelTabel;
    private BarangDAO bDAO = new BarangDAO();
    private int statusSementara = 1;

    public FormBarang() {
        // === JUDUL JENDELA ====
        setTitle("Kelola Data Barang - Toko Olahraga");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // === PANEL INPUT DATA BARANG ==== 
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

        add(panelInput, BorderLayout.NORTH);
        

        // ==== PANEL TABEL TENGAH ====
        String[] kolom = {"ID", "Nama", "Harga Jual", "Harga Modal", "Jenis", "Brand", "Warna", "Stok", "Status"};
        modelTabel = new DefaultTableModel(kolom, 0);
        tabelBarang = new JTable(modelTabel);
        add(new JScrollPane(tabelBarang), BorderLayout.CENTER);

        // ==== PANEL TOMBOL ====
        JPanel panelTombol = new JPanel();
        JButton btnSimpan = new JButton("SIMPAN BARU");
        JButton btnEdit = new JButton("UPDATE DATA");
        JButton btnStatus = new JButton("AKTIF/NON-AKTIF");
        JButton btnRefresh = new JButton("REFRESH DATA");
        
        panelTombol.add(btnSimpan);
        panelTombol.add(btnEdit);
        panelTombol.add(btnStatus);
        panelTombol.add(btnRefresh);
        add(panelTombol, BorderLayout.SOUTH);

        JPanel panelKiri = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBack = new JButton("KEMBALI");
        btnBack.setBackground(Color.DARK_GRAY);
        btnBack.setForeground(Color.WHITE);
        btnBack.addActionListener(e -> this.dispose());
        panelKiri.add(btnBack);

        JPanel panelBawahFinal = new JPanel(new BorderLayout());
        panelBawahFinal.add(panelKiri, BorderLayout.WEST);   // Back di pojok kiri
        panelBawahFinal.add(panelTombol, BorderLayout.CENTER); // CRUD di tengah
        
        add(panelBawahFinal, BorderLayout.SOUTH);

       

        // Event Tombol Simpan Baru
    tabelBarang.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int baris = tabelBarang.getSelectedRow();
                if(baris != -1){
                    String id = modelTabel.getValueAt(baris, 0).toString();
                    Barang b = bDAO.getById(id);
                    if(b != null){
                        txtId.setText(b.getIdBarang());
                        txtId.setEditable(false); // ID jangan boleh diedit
                        txtNama.setText(b.getNamaBarang());
                        txtHargaJual.setText(String.valueOf(b.getHargaJual()));
                        txtHargaModal.setText(String.valueOf(b.getHargaModal()));
                        txtJenis.setText(b.getJenisBarang());
                        txtBrand.setText(b.getBrand());
                        txtWarna.setText(b.getWarna());
                        txtStok.setText(String.valueOf(b.getStok()));

                        statusSementara = b.getStatus();
                    }
                }
            }
        });

        // 2. Simpan Baru
        btnSimpan.addActionListener(e -> aksiSimpan());

        // 3. Update Data
        btnEdit.addActionListener(e -> aksiUpdate());

        // 4. Ubah Status (Toggle)
        btnStatus.addActionListener(e -> aksiGantiStatus());

        // 5. Refresh
        btnRefresh.addActionListener(e -> {
            loadData();
            clearFields();
        });

        loadData();
    }

    private void aksiSimpan() {
        try {
            Barang b = ambilDataDariInput();
            b.setStatus(1); // Default aktif
            bDAO.insert(b);
            
            KoneksiDatabase.addLog(UserSession.getIdAkun(), "Simpan Barang Baru: " + b.getNamaBarang());

            JOptionPane.showMessageDialog(this, "Barang Berhasil Disimpan!");
            loadData();
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal Simpan: " + ex.getMessage());
        }
    }

    private void aksiUpdate() {
        try {
            Barang b = ambilDataDariInput();
            bDAO.update(b);
            KoneksiDatabase.addLog(UserSession.getIdAkun(), "Update Barang: " + b.getNamaBarang());
            JOptionPane.showMessageDialog(this, "Data Berhasil Diperbarui!");
            loadData();
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal Update: " + ex.getMessage());
        }
    }

    private void aksiGantiStatus() {
        int baris = tabelBarang.getSelectedRow();
        if (baris != -1) {
            String id = modelTabel.getValueAt(baris, 0).toString();
            String namaBarang = modelTabel.getValueAt(baris, 1).toString(); 
            String statusSekarang = modelTabel.getValueAt(baris, 8).toString();
            
            int statusBaru = statusSekarang.equals("AKTIF") ? 0 : 1;
            String labelStatus = (statusBaru == 1) ? "AKTIF" : "NON-AKTIF"; 
            String pesan = (statusBaru == 0) ? "Non-aktifkan barang [" + namaBarang + "]?" : "Aktifkan kembali barang [" + namaBarang + "]?";
            if (JOptionPane.showConfirmDialog(this, pesan, "Konfirmasi", JOptionPane.YES_NO_OPTION) == 0) {
                
                bDAO.updateStatus(id, statusBaru);
                
                KoneksiDatabase.addLog(UserSession.getIdAkun(), "Ubah Status Barang [" + namaBarang + "] jadi " + labelStatus);
                
                loadData();
                clearFields();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih barang di tabel dulu!");
        }
    }

    private Barang ambilDataDariInput() {
        return new Barang(
            txtId.getText(), 
            txtNama.getText(),
            Double.parseDouble(txtHargaJual.getText()),
            Double.parseDouble(txtHargaModal.getText()),
            txtJenis.getText(), 
            txtBrand.getText(),
            txtWarna.getText(), 
            Integer.parseInt(txtStok.getText()),
            statusSementara
        );
    }

    private void clearFields() {
        txtId.setText("");
        txtId.setEditable(true);
        txtNama.setText("");
        txtHargaJual.setText("");
        txtHargaModal.setText("");
        txtJenis.setText("");
        txtBrand.setText("");
        txtWarna.setText("");
        txtStok.setText("");
        tabelBarang.clearSelection();
    }

    private void loadData() {
        modelTabel.setRowCount(0); // Kosongin tabel dulu
        List<Barang> list = bDAO.getAll();
        for (Barang b : list) {
            String statusTeks = (b.getStatus() == 1) ? "AKTIF" : "NON-AKTIF";

            modelTabel.addRow(new Object[]{
                b.getIdBarang(), b.getNamaBarang(), b.getHargaJual(),
                b.getHargaModal(), b.getJenisBarang(), b.getBrand(),
                b.getWarna(), b.getStok(),
                statusTeks
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
