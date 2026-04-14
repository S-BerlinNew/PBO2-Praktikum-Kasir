package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import dao.BarangDAO;
import model.Barang;
import java.awt.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class FormBarang extends JFrame{

    // Komponen
    private JTextField txtId, txtNama, txtHargaJual, txtHargaModal, txtJenis, txtBrand, txtWarna, txtStok, txtDiskon;
    private JTable tabelBarang;
    private DefaultTableModel modelTabel;
    private BarangDAO bDAO = new BarangDAO();
    private int statusSementara = 1;

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
        String[] kolom = {"ID", "Nama", "Harga Jual", "Harga Modal", "Jenis", "Brand", "Warna", "Stok", "Diskon", "Status"};
        modelTabel = new DefaultTableModel(kolom, 0);
        tabelBarang = new JTable(modelTabel);
        add(new JScrollPane(tabelBarang), BorderLayout.CENTER);

        // Panel Tombol
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
                        txtDiskon.setText(String.valueOf(b.getDiskon()));

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
            String statusSekarang = modelTabel.getValueAt(baris, 9).toString();
            
            int statusBaru = statusSekarang.equals("AKTIF") ? 0 : 1;
            String pesan = (statusBaru == 0) ? "Non-aktifkan barang?" : "Aktifkan kembali barang?";
            
            if (JOptionPane.showConfirmDialog(this, pesan, "Konfirmasi", JOptionPane.YES_NO_OPTION) == 0) {
                bDAO.updateStatus(id, statusBaru);
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
            Double.parseDouble(txtDiskon.getText()), 
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
        txtDiskon.setText("0");
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
                b.getWarna(), b.getStok(), b.getDiskon() + "%",
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
