package com.kasir.app.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.kasir.app.config.KoneksiDatabase;
import com.kasir.app.dao.AkunDAO;
import com.kasir.app.model.Akun;
import com.kasir.app.model.UserSession;

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

        // 
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
        panelBawahFinal.add(panelKiri, BorderLayout.WEST);   
        panelBawahFinal.add(panelTombol, BorderLayout.CENTER); 
        
        add(panelBawahFinal, BorderLayout.SOUTH);


        // BAGIAN LOGIKA BUTTON
        btnSimpan.addActionListener(e -> tampilkanFormInput(null));
        
        btnUpdate.addActionListener(e -> {
            if(idDipilih != -1) {
                Akun a = new Akun(idDipilih, txtUsername.getText(), "", txtNama.getText(), cbRole.getSelectedItem().toString());
                tampilkanFormInput(a);
            } else {
                JOptionPane.showMessageDialog(this, "Pilih akun ditabel terlebih dahulu!");
            }
        });
        
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

    public void tampilkanFormInput(Akun dataEdit) {
        String judul = (dataEdit == null) ? "Tambah Akun Baru" : "Update Akun: " + dataEdit.getUsername();
        JDialog d = new JDialog(this, judul, true);
        d.setSize(400, 300);
        d.setLocationRelativeTo(this);
        d.setLayout(new GridLayout(5, 2, 10, 10));
        ((JPanel)d.getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField tUser = new JTextField();
        JPasswordField tPass = new JPasswordField();
        JTextField tNama = new JTextField();
        JComboBox<String> cRole = new JComboBox<>(new String[]{"admin", "kasir"});

        if (dataEdit != null) {
            tUser.setText(dataEdit.getUsername());
            tUser.setEditable(false); // Username jangan diganti pas update
            tNama.setText(dataEdit.getNamaLengkap());
            cRole.setSelectedItem(dataEdit.getRole());
        }

        d.add(new JLabel("Username:")); d.add(tUser);
        d.add(new JLabel("Password:")); d.add(tPass);
        d.add(new JLabel("Nama Lengkap:")); d.add(tNama);
        d.add(new JLabel("Role:")); d.add(cRole);

        JButton bSimpan = new JButton("OK, SIMPAN");
        bSimpan.addActionListener(ev -> {
            String pass = new String(tPass.getPassword());
            
            if (dataEdit == null) {
                // --- LOGIKA INSERT ---
                Akun baru = new Akun(0, tUser.getText(), pass, tNama.getText(), cRole.getSelectedItem().toString());
                if(aDAO.insert(baru)) {
                    // CATAT LOG TAMBAH AKUN
                    KoneksiDatabase.addLog(UserSession.getIdAkun(), "Menambah Akun Baru: " + tUser.getText());
                    
                    JOptionPane.showMessageDialog(d, "Akun Berhasil Dibuat!");
                    d.dispose();
                }
            } else {
                // --- LOGIKA UPDATE ---
                dataEdit.setNamaLengkap(tNama.getText());
                dataEdit.setRole(cRole.getSelectedItem().toString());
                if(!pass.isEmpty()) dataEdit.setPassword(pass); 
                
                if(aDAO.update(dataEdit)) {
                    // CATAT LOG UPDATE AKUN
                    String pesanLog = "Update Akun: " + dataEdit.getUsername();
                    if(!pass.isEmpty()) pesanLog += " (Ganti Password)";
                    
                    KoneksiDatabase.addLog(UserSession.getIdAkun(), pesanLog);
                    
                    JOptionPane.showMessageDialog(d, "Akun Berhasil Diperbarui!");
                    d.dispose();
                }
            }
            muatData();
        });

        d.add(new JLabel("")); d.add(bSimpan);
        d.setVisible(true);
    }


    private void hapusAkun() {
        if(idDipilih == -1) {
            JOptionPane.showMessageDialog(this, "Pilih akun di tabel dulu!");
            return;
        }
        String usernameTarget = modelTabel.getValueAt(tabelAkun.getSelectedRow(), 1).toString();

        int konfirm = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin menghapus akun [" + usernameTarget + "]?", "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION);
            
        if(konfirm == JOptionPane.YES_OPTION) {
            if(aDAO.delete(idDipilih)) { 
                
                KoneksiDatabase.addLog(UserSession.getIdAkun(), "MENGHAPUS AKUN: " + usernameTarget);
                
                JOptionPane.showMessageDialog(this, "Akun Berhasil Dihapus!");
                idDipilih = -1; 
                muatData();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus akun!");
            }
        }
    }
}
