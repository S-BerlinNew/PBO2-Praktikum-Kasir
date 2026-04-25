package ui;

import dao.BarangDAO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Barang;

public class FormBarang extends JPanel {

    private JTextField txtId, txtNama, txtHargaJual, txtHargaModal, txtJenis, txtBrand, txtWarna, txtStok;
    private JTable tabelBarang;
    private DefaultTableModel modelTabel;
    private BarangDAO bDAO = new BarangDAO();
    private int statusSementara = 1;

    public FormBarang() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ================= TITLE =================
        JLabel title = new JLabel("KELOLA DATA BARANG");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(44, 62, 80));

        // ================= PANEL INPUT =================
        JPanel panelInput = new JPanel(new GridLayout(3, 6, 10, 10));
        panelInput.setBackground(Color.WHITE);
        panelInput.setBorder(BorderFactory.createTitledBorder("Input Data Barang"));

        txtId = new JTextField();
        txtNama = new JTextField();
        txtHargaJual = new JTextField();
        txtHargaModal = new JTextField();
        txtJenis = new JTextField();
        txtBrand = new JTextField();
        txtWarna = new JTextField();
        txtStok = new JTextField();

        panelInput.add(new JLabel("ID Barang:"));
        panelInput.add(txtId);
        panelInput.add(new JLabel("Nama Barang:"));
        panelInput.add(txtNama);
        panelInput.add(new JLabel("Harga Jual:"));
        panelInput.add(txtHargaJual);
        panelInput.add(new JLabel("Harga Modal:"));
        panelInput.add(txtHargaModal);
        panelInput.add(new JLabel("Jenis:"));
        panelInput.add(txtJenis);
        panelInput.add(new JLabel("Brand:"));
        panelInput.add(txtBrand);
        panelInput.add(new JLabel("Warna:"));
        panelInput.add(txtWarna);
        panelInput.add(new JLabel("Stok:"));
        panelInput.add(txtStok);

        // ================= TABEL =================
        String[] kolom = {"ID", "Nama", "Harga Jual", "Harga Modal", "Jenis", "Brand", "Warna", "Stok", "Status"};
        modelTabel = new DefaultTableModel(kolom, 0);
        tabelBarang = new JTable(modelTabel);

        JScrollPane scroll = new JScrollPane(tabelBarang);

        // ================= BUTTON =================
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelTombol.setBackground(new Color(245, 247, 250));

        JButton btnSimpan = new JButton("SIMPAN");
        JButton btnEdit = new JButton("UPDATE");
        JButton btnStatus = new JButton("STATUS");
        JButton btnRefresh = new JButton("REFRESH");

        panelTombol.add(btnSimpan);
        panelTombol.add(btnEdit);
        panelTombol.add(btnStatus);
        panelTombol.add(btnRefresh);

        // ================= WRAPPER ATAS =================
        JPanel atas = new JPanel(new BorderLayout(10,10));
        atas.setOpaque(false);
        atas.add(title, BorderLayout.NORTH);
        atas.add(panelInput, BorderLayout.CENTER);

        add(atas, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelTombol, BorderLayout.SOUTH);

        // ================= EVENT =================

        tabelBarang.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int baris = tabelBarang.getSelectedRow();
                if (baris != -1) {
                    String id = modelTabel.getValueAt(baris, 0).toString();
                    Barang b = bDAO.getById(id);
                    if (b != null) {
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

    private void aksiSimpan() {
        try {
            Barang b = ambilDataDariInput();
            b.setStatus(1);
            bDAO.insert(b);
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
            String statusSekarang = modelTabel.getValueAt(baris, 8).toString();

            int statusBaru = statusSekarang.equals("AKTIF") ? 0 : 1;

            if (JOptionPane.showConfirmDialog(this, "Ubah status?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == 0) {
                bDAO.updateStatus(id, statusBaru);
                loadData();
                clearFields();
            }
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
            String statusTeks = (b.getStatus() == 1) ? "AKTIF" : "NON-AKTIF";

            modelTabel.addRow(new Object[]{
                    b.getIdBarang(), b.getNamaBarang(), b.getHargaJual(),
                    b.getHargaModal(), b.getJenisBarang(), b.getBrand(),
                    b.getWarna(), b.getStok(),
                    statusTeks
            });
        }
    }
}