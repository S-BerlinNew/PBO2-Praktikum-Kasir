package ui;

import dao.AkunDAO;
import model.Akun;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FormKelolaAkunPanel extends JPanel {

    private JTable tabelAkun;
    private DefaultTableModel modelTabel;
    private AkunDAO aDAO = new AkunDAO();
    private int idDipilih = -1;

    public FormKelolaAkunPanel() {

        setLayout(new BorderLayout(15,15));
        setBackground(new Color(245,247,250));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // ================= TITLE =================
        JLabel title = new JLabel("KELOLA AKUN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(44,62,80));

        // ================= TABEL =================
        String[] kolom = {"ID", "Username", "Nama Lengkap", "Role"};
        modelTabel = new DefaultTableModel(kolom, 0);
        tabelAkun = new JTable(modelTabel);

        JScrollPane scroll = new JScrollPane(tabelAkun);

        // ================= BUTTON =================
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelTombol.setBackground(new Color(245,247,250));

        JButton btnTambah = new JButton("TAMBAH");
        JButton btnUpdate = new JButton("UPDATE");
        JButton btnHapus = new JButton("HAPUS");

        panelTombol.add(btnTambah);
        panelTombol.add(btnUpdate);
        panelTombol.add(btnHapus);

        // ================= WRAPPER =================
        JPanel atas = new JPanel(new BorderLayout());
        atas.setOpaque(false);
        atas.add(title, BorderLayout.NORTH);

        add(atas, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelTombol, BorderLayout.SOUTH);

        // ================= EVENT =================
        tabelAkun.getSelectionModel().addListSelectionListener(e -> {
            int row = tabelAkun.getSelectedRow();
            if(row != -1) {
                idDipilih = Integer.parseInt(modelTabel.getValueAt(row, 0).toString());
            }
        });

        btnTambah.addActionListener(e -> tampilkanFormInput(null));

        btnUpdate.addActionListener(e -> {
            if(idDipilih != -1) {
                Akun a = aDAO.getById(idDipilih); // pastikan DAO kamu ada method ini
                tampilkanFormInput(a);
            } else {
                JOptionPane.showMessageDialog(this, "Pilih akun dulu!");
            }
        });

        btnHapus.addActionListener(e -> hapusAkun());

        muatData();
    }

    private void muatData() {
        modelTabel.setRowCount(0);
        List<Akun> list = aDAO.getAll();

        for (Akun a : list) {
            modelTabel.addRow(new Object[]{
                    a.getIdAkun(),
                    a.getUsername(),
                    a.getNamaLengkap(),
                    a.getRole()
            });
        }
    }

    // ================= DIALOG INPUT =================
    public void tampilkanFormInput(Akun dataEdit) {

        String judul = (dataEdit == null) ? "Tambah Akun" : "Update Akun";

        JDialog d = new JDialog();
        d.setTitle(judul);
        d.setSize(400, 300);
        d.setLocationRelativeTo(this);
        d.setLayout(new GridLayout(5,2,10,10));

        JTextField tUser = new JTextField();
        JPasswordField tPass = new JPasswordField();
        JTextField tNama = new JTextField();
        JComboBox<String> cRole = new JComboBox<>(new String[]{"admin","kasir"});

        if(dataEdit != null){
            tUser.setText(dataEdit.getUsername());
            tUser.setEditable(false);
            tNama.setText(dataEdit.getNamaLengkap());
            cRole.setSelectedItem(dataEdit.getRole());
        }

        d.add(new JLabel("Username"));
        d.add(tUser);
        d.add(new JLabel("Password"));
        d.add(tPass);
        d.add(new JLabel("Nama"));
        d.add(tNama);
        d.add(new JLabel("Role"));
        d.add(cRole);

        JButton btnSimpan = new JButton("SIMPAN");

        btnSimpan.addActionListener(e -> {
            String pass = new String(tPass.getPassword());

            if(dataEdit == null){
                Akun baru = new Akun(0, tUser.getText(), pass, tNama.getText(), cRole.getSelectedItem().toString());
                aDAO.insert(baru);
                JOptionPane.showMessageDialog(d,"Akun berhasil dibuat");
            } else {
                dataEdit.setNamaLengkap(tNama.getText());
                dataEdit.setRole(cRole.getSelectedItem().toString());
                if(!pass.isEmpty()) dataEdit.setPassword(pass);

                aDAO.update(dataEdit);
                JOptionPane.showMessageDialog(d,"Akun berhasil diupdate");
            }

            muatData();
            d.dispose();
        });

        d.add(new JLabel(""));
        d.add(btnSimpan);

        d.setVisible(true);
    }

    private void hapusAkun() {
        if(idDipilih == -1){
            JOptionPane.showMessageDialog(this,"Pilih akun dulu!");
            return;
        }

        int konfirm = JOptionPane.showConfirmDialog(this,"Yakin hapus?");

        if(konfirm == JOptionPane.YES_OPTION){
            aDAO.delete(idDipilih);
            JOptionPane.showMessageDialog(this,"Berhasil dihapus");
            idDipilih = -1;
            muatData();
        }
    }
}