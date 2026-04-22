package ui;

import dao.AkunDAO;
import model.Akun;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FormKelolaAkun extends JDialog {
    private JTable tabelAkun;
    private DefaultTableModel modelTabel;
    private JTextField txtUsername, txtNama;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;
    private AkunDAO aDAO = new AkunDAO();
    private int idDipilih = -1;

    public FormKelolaAkun(Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Kelola Akun Admin & Kasir");
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10,10));

        // BAGIAN ATAS(PARENT);
        String[] kolom = {"ID", "Username", " Nama Lengkap", " Role"};
        modelTabel = new DefaultTableModel(kolom, 0);
        tabelAkun = new JTable(modelTabel);
        add(new JScrollPane(tabelAkun), BorderLayout.CENTER);

        // BAGIAN BAWAH (FORM INPUT)
        JPanel panelInput = new JPanel(new GridLayout(5, 2, 10, 10));
        panelInput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelInput.add(new JLabel("Username : "));
        txtUsername = new JTextField();
        panelInput.add(txtUsername);

        panelInput.add(new JLabel("Password : "));
        txtPassword = new JPasswordField();
        panelInput.add(txtPassword);

        panelInput.add(new JLabel("Nama Lengkap:"));
        txtNama = new JTextField();
        panelInput.add(txtNama);

        panelInput.add(new JLabel("Role:"));
        cbRole = new JComboBox<>(new String[]{"admin", "kasir"});
        panelInput.add(cbRole);

        // Tombol-tombol
        JButton btnSimpan = new JButton("Simpan Baru");
        JButton btnUpdate = new JButton("Update (Ganti Pass)");
        JButton btnHapus = new JButton("Hapus Akun");

        JPanel panelTombol = new JPanel();
        panelTombol.add(btnSimpan);
        panelTombol.add(btnUpdate);
        panelTombol.add(btnHapus);

        JPanel panelBawah = new JPanel(new BorderLayout());
        panelBawah.add(panelInput, BorderLayout.CENTER);
        panelBawah.add(panelTombol, BorderLayout.SOUTH);

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

        // BAGIAN LOGIKA BUTTON
        btnSimpan.addActionListener(e -> simpanAkun());
        
        btnHapus.addActionListener(e -> hapusAkun());

        //Event klik tabel untul ambil data
        tabelAkun.getSelectionModel().addListSelectionListener(e -> {
            int row = tabelAkun.getSelectedRow();
            if(row != -1) {
                idDipilih = Integer.parseInt(modelTabel.getValueAt(row, 0).toString());
                txtUsername.setText(modelTabel.getValueAt(row, 1).toString());
                txtNama.setText(modelTabel.getValueAt(row, 2).toString());
                cbRole.setSelectedItem(modelTabel.getValueAt(row, 3).toString());
            }
        });
        muatData();
    }

    private void muatData() {
        modelTabel.setRowCount(0);
        modelTabel.setRowCount(0);
        List<Akun> list = aDAO.getAll();
        for (Akun a : list) {
            Object[] row = { a.getIdAkun(), a.getUsername(), a.getNamaLengkap(), a.getRole() };
            modelTabel.addRow(row);
        }
    }

    private void simpanAkun() {
        Akun a = new Akun();
        a.setUsername(txtUsername.getText());
        a.setPassword(new String(txtPassword.getPassword()));
        a.setNamaLengkap(txtNama.getText());
        a.setRole(cbRole.getSelectedItem().toString());

        if(aDAO.insert(a)) {
            JOptionPane.showMessageDialog(this, "Akun Berhasil Dibuat!");
            muatData();
        }
    }

    private void hapusAkun() {
        if(idDipilih == -1) return;
        int konfirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin mengapus akun ini?", "Konfirmasi", JOptionPane.YES_NO_CANCEL_OPTION);
        if(konfirm == JOptionPane.YES_NO_OPTION) {
            muatData();
        }
    }
}
