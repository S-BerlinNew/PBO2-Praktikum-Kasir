package com.kasir.app.ui;

import com.kasir.app.dao.CustomerDAO;
import com.kasir.app.model.Customer;
import com.kasir.app.model.UserSession;
import com.kasir.app.config.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FormCustomer extends JFrame {
    private JTextField txtNama, txtNoTelp, txtKodeCustomer;
    private JTable tabelCustomer;
    private DefaultTableModel modelTabel;
    private CustomerDAO cDAO = new CustomerDAO();
    private JButton btnEdit, btnSimpan, btnRefresh, btnHapus;

    public FormCustomer() {
        setTitle("Data Pelanggan - Toko Olahraga");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Panel Input ---
        JPanel panelInput = new JPanel(new GridLayout(3, 2, 5, 5));
        panelInput.setBorder(BorderFactory.createTitledBorder("Input Customer"));
        
        panelInput.add(new JLabel("Kode Customer:"));
        txtKodeCustomer = new JTextField(); // Inisialisasi variabel yang benar
        txtKodeCustomer.setEditable(false); // Jangan biarkan user edit manual
        txtKodeCustomer.setBackground(new Color(235, 235, 235)); // Kasih warna abu biar kelihatan auto
        panelInput.add(txtKodeCustomer);

        panelInput.add(new JLabel("Nama Pelanggan:"));
        txtNama = new JTextField();
        panelInput.add(txtNama);

        panelInput.add(new JLabel("No. Telepon:"));
        txtNoTelp = new JTextField();
        panelInput.add(txtNoTelp);

        add(panelInput, BorderLayout.NORTH);

        // --- Tabel ---
        String[] kolom = {"ID", "Kode", "Nama", "No. Telp"};
        modelTabel = new DefaultTableModel(kolom, 0);
        tabelCustomer = new JTable(modelTabel);
        add(new JScrollPane(tabelCustomer), BorderLayout.CENTER);

        // --- Tombol ---
        JPanel panelTombol = new JPanel();
        btnSimpan = new JButton("SIMPAN");
        btnRefresh = new JButton("REFRESH");
        btnEdit = new JButton("Edit");
        btnHapus = new JButton("Hapus");
        panelTombol.add(btnSimpan);
        panelTombol.add(btnRefresh);
        panelTombol.add(btnEdit);
        panelTombol.add(btnHapus);

        // --- LOGIKA ROLE ---
        String roleSekarang = UserSession.getRole();
        System.out.println("DEBUG: Role di FormCustomer adalah: " + roleSekarang);

        if ("Kasir".equalsIgnoreCase(roleSekarang)) {
            btnEdit.setVisible(false);
            btnHapus.setVisible(false);
        } else {
            btnEdit.setVisible(true);
            btnHapus.setVisible(true);
        }

        JPanel panelKiri = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBack = new JButton("KEMBALI");
        btnBack.setBackground(Color.DARK_GRAY);
        btnBack.setForeground(Color.WHITE);
        btnBack.addActionListener(e -> this.dispose());
        panelKiri.add(btnBack);

        JPanel panelBawahFinal = new JPanel(new BorderLayout());
        panelBawahFinal.add(panelKiri, BorderLayout.WEST);   // Back di pojok kiri
        panelBawahFinal.add(panelTombol, BorderLayout.CENTER); // CRUD di tengah
        
        // 4. TERAKHIR: Masukkan satu panel utama ini ke SOUTH
        add(panelBawahFinal, BorderLayout.SOUTH);



        // --- Event Handling ---
        loadData();
        setKodeOtomatis();

        tabelCustomer.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // Tambahkan pengecekan ini
                int baris = tabelCustomer.getSelectedRow();
                if (baris != -1) {
                    txtKodeCustomer.setText(modelTabel.getValueAt(baris, 1).toString());
                    txtNama.setText(modelTabel.getValueAt(baris, 2).toString());
                    txtNoTelp.setText(modelTabel.getValueAt(baris, 3).toString());
                }
            }
        });

        btnRefresh.addActionListener(e -> {
            loadData();
            txtNama.setText("");
            txtNoTelp.setText("");
            setKodeOtomatis(); // <--- Biar kodenya update kalau ada data baru masuk
        });

        btnSimpan.addActionListener(e -> {
            Customer c = new Customer();
            c.setKodeCustomer(txtKodeCustomer.getText()); 
            c.setNamaCustomer(txtNama.getText());
            c.setNoTelp(txtNoTelp.getText());
            
            cDAO.insert(c); 
            
            KoneksiDatabase.addLog(UserSession.getIdAkun(), "Tambah Customer: " + c.getNamaCustomer());
            
            JOptionPane.showMessageDialog(this, "Customer Berhasil Ditambah!");
            loadData();
            setKodeOtomatis(); 
            
            txtNama.setText("");
            txtNoTelp.setText("");
        });

        btnEdit.addActionListener(e -> {
            int baris = tabelCustomer.getSelectedRow();
            if (baris == -1) {
                JOptionPane.showMessageDialog(this, "Pilih dulu data di tabel yang mau diedit!");
                return;
            }

            // Ambil data BARU dari TextField
            int id = Integer.parseInt(modelTabel.getValueAt(baris, 0).toString());
            String namaBaru = txtNama.getText();
            String telpBaru = txtNoTelp.getText();

            Customer c = new Customer();
            c.setIdCustomer(id);
            c.setNamaCustomer(namaBaru);
            c.setNoTelp(telpBaru);

            tabelCustomer.clearSelection(); 
            cDAO.update(c); 

            KoneksiDatabase.addLog(UserSession.getIdAkun(), "Update Customer: " + namaBaru);
            JOptionPane.showMessageDialog(this, "Data " + namaBaru + " berhasil diperbarui!");

            loadData(); 
            
            // Reset field
            txtNama.setText("");
            txtNoTelp.setText("");
            setKodeOtomatis(); 
        });

        btnHapus.addActionListener(e -> {
            int baris = tabelCustomer.getSelectedRow();
            if (baris == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data yang mau dihapus!");
                return;
            }
            
            int id = Integer.parseInt(modelTabel.getValueAt(baris, 0).toString());
            String namaHapus = modelTabel.getValueAt(baris, 2).toString(); // Ambil nama dari kolom ke-3 (index 2)
            
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin mau hapus " + namaHapus + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                cDAO.delete(id);
                
                KoneksiDatabase.addLog(UserSession.getIdAkun(), "Hapus Customer: " + namaHapus);
                
                JOptionPane.showMessageDialog(this, "Data Berhasil Dihapus!");
                loadData();
                setKodeOtomatis();
                txtNama.setText("");
                txtNoTelp.setText("");
            }
        });
    }

    

    private void setKodeOtomatis() {
        String kodeBaru = cDAO.generateKodeBaru();
        txtKodeCustomer.setText(kodeBaru); // Pastikan nama variabelnya txtKodeCustomer
        txtKodeCustomer.setEditable(false); // Biar nggak bisa dihapus user
    }


    private void loadData() {
        modelTabel.setRowCount(0);
        List<Customer> list = cDAO.getAll();
        for (Customer c : list) {
            modelTabel.addRow(new Object[]{c.getIdCustomer(), c.getKodeCustomer(), c.getNamaCustomer(), c.getNoTelp()});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormCustomer().setVisible(true));
    }
}