package com.kasir.app.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.kasir.app.config.KoneksiDatabase;
import com.kasir.app.dao.BarangDAO;
import com.kasir.app.model.Barang;
import com.kasir.app.model.UserSession;

public class FormBarang extends JPanel {

    private JTextField txtId, txtNama, txtHargaJual, txtHargaModal, txtJenis, txtBrand, txtWarna, txtStok;
    private JTable tabelBarang;
    private DefaultTableModel modelTabel;
    private BarangDAO bDAO = new BarangDAO();
    private int statusSementara = 1;

    public FormBarang() {

        setLayout(new BorderLayout(10,10));
        setBackground(Color.WHITE);

        // ================= PANEL INPUT =================
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

        // ================= TABEL =================
        String[] kolom = {"ID", "Nama", "Harga Jual", "Harga Modal", "Jenis", "Brand", "Warna", "Stok", "Status"};
        modelTabel = new DefaultTableModel(kolom, 0);
        tabelBarang = new JTable(modelTabel);

        add(new JScrollPane(tabelBarang), BorderLayout.CENTER);

        // ================= TOMBOL =================
        JPanel panelTombol = new JPanel();

        JButton btnSimpan = new JButton("SIMPAN");
        JButton btnEdit = new JButton("UPDATE");
        JButton btnStatus = new JButton("STATUS");
        JButton btnRefresh = new JButton("REFRESH");

        panelTombol.add(btnSimpan);
        panelTombol.add(btnEdit);
        panelTombol.add(btnStatus);
        panelTombol.add(btnRefresh);

        add(panelTombol, BorderLayout.SOUTH);

        // ================= EVENT =================

        tabelBarang.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int baris = tabelBarang.getSelectedRow();
                if(baris != -1){
                    String id = modelTabel.getValueAt(baris, 0).toString();
                    Barang b = bDAO.getById(id);
                    if(b != null){
                        txtId.setText(b.getIdBarang());
                        txtId.setEditable(false);
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

        btnSimpan.addActionListener(e -> aksiSimpan());
        btnEdit.addActionListener(e -> aksiUpdate());
        btnStatus.addActionListener(e -> aksiGantiStatus());

        btnRefresh.addActionListener(e -> {
            loadData();
            clearFields();
        });

        loadData();
    }

    // ================= LOGIC =================

    private void aksiSimpan() {
        try {
            Barang b = ambilDataDariInput();
            b.setStatus(1);
            bDAO.insert(b);

            KoneksiDatabase.addLog(UserSession.getIdAkun(), "Simpan Barang: " + b.getNamaBarang());

            JOptionPane.showMessageDialog(this, "Berhasil Simpan!");
            loadData();
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void aksiUpdate() {
        try {
            Barang b = ambilDataDariInput();
            bDAO.update(b);

            KoneksiDatabase.addLog(UserSession.getIdAkun(), "Update Barang: " + b.getNamaBarang());

            JOptionPane.showMessageDialog(this, "Berhasil Update!");
            loadData();
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void aksiGantiStatus() {
        int baris = tabelBarang.getSelectedRow();
        if (baris != -1) {
            String id = modelTabel.getValueAt(baris, 0).toString();
            String statusSekarang = modelTabel.getValueAt(baris, 8).toString();

            int statusBaru = statusSekarang.equals("AKTIF") ? 0 : 1;

            bDAO.updateStatus(id, statusBaru);
            loadData();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data dulu!");
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
        modelTabel.setRowCount(0);
        List<Barang> list = bDAO.getAll();

        for (Barang b : list) {
            String status = (b.getStatus() == 1) ? "AKTIF" : "NON-AKTIF";

            modelTabel.addRow(new Object[]{
                b.getIdBarang(), b.getNamaBarang(),
                b.getHargaJual(), b.getHargaModal(),
                b.getJenisBarang(), b.getBrand(),
                b.getWarna(), b.getStok(), status
            });
        }
    }
}