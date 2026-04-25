package com.kasir.app.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
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
import com.kasir.app.dao.CustomerDAO;
import com.kasir.app.model.Customer;
import com.kasir.app.model.UserSession;

public class FormCustomer extends JPanel {

    private JTextField txtNama, txtNoTelp, txtKodeCustomer;
    private JTable tabelCustomer;
    private DefaultTableModel modelTabel;
    private CustomerDAO cDAO = new CustomerDAO();

    private JButton btnEdit, btnSimpan, btnRefresh, btnHapus;

    public FormCustomer() {

        setLayout(new BorderLayout(10,10));
        setBackground(Color.WHITE);

        // ================= INPUT =================
        JPanel panelInput = new JPanel(new GridLayout(3, 2, 5, 5));
        panelInput.setBorder(BorderFactory.createTitledBorder("Input Customer"));

        panelInput.add(new JLabel("Kode Customer:"));
        txtKodeCustomer = new JTextField();
        txtKodeCustomer.setEditable(false);
        txtKodeCustomer.setBackground(new Color(235,235,235));
        panelInput.add(txtKodeCustomer);

        panelInput.add(new JLabel("Nama Pelanggan:"));
        txtNama = new JTextField();
        panelInput.add(txtNama);

        panelInput.add(new JLabel("No. Telepon:"));
        txtNoTelp = new JTextField();
        panelInput.add(txtNoTelp);

        add(panelInput, BorderLayout.NORTH);

        // ================= TABEL =================
        String[] kolom = {"ID", "Kode", "Nama", "No. Telp"};
        modelTabel = new DefaultTableModel(kolom, 0);
        tabelCustomer = new JTable(modelTabel);

        add(new JScrollPane(tabelCustomer), BorderLayout.CENTER);

        // ================= BUTTON =================
        JPanel panelTombol = new JPanel();

        btnSimpan = new JButton("SIMPAN");
        btnRefresh = new JButton("REFRESH");
        btnEdit = new JButton("EDIT");
        btnHapus = new JButton("HAPUS");

        panelTombol.add(btnSimpan);
        panelTombol.add(btnRefresh);
        panelTombol.add(btnEdit);
        panelTombol.add(btnHapus);

        add(panelTombol, BorderLayout.SOUTH);

        // ================= ROLE =================
        String role = UserSession.getRole();

        if ("Kasir".equalsIgnoreCase(role)) {
            btnEdit.setVisible(false);
            btnHapus.setVisible(false);
        }

        // ================= LOAD =================
        loadData();
        setKodeOtomatis();

        // ================= EVENT =================

        tabelCustomer.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
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
            clear();
            setKodeOtomatis();
        });

        btnSimpan.addActionListener(e -> {
            Customer c = new Customer();
            c.setKodeCustomer(txtKodeCustomer.getText());
            c.setNamaCustomer(txtNama.getText());
            c.setNoTelp(txtNoTelp.getText());

            cDAO.insert(c);

            KoneksiDatabase.addLog(UserSession.getIdAkun(), "Tambah Customer: " + c.getNamaCustomer());

            JOptionPane.showMessageDialog(this, "Berhasil tambah customer!");
            loadData();
            clear();
            setKodeOtomatis();
        });

        btnEdit.addActionListener(e -> {
            int baris = tabelCustomer.getSelectedRow();
            if (baris == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data dulu!");
                return;
            }

            int id = Integer.parseInt(modelTabel.getValueAt(baris, 0).toString());

            Customer c = new Customer();
            c.setIdCustomer(id);
            c.setNamaCustomer(txtNama.getText());
            c.setNoTelp(txtNoTelp.getText());

            cDAO.update(c);

            KoneksiDatabase.addLog(UserSession.getIdAkun(), "Update Customer: " + c.getNamaCustomer());

            JOptionPane.showMessageDialog(this, "Berhasil update!");
            loadData();
            clear();
            setKodeOtomatis();
        });

        btnHapus.addActionListener(e -> {
            int baris = tabelCustomer.getSelectedRow();
            if (baris == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data dulu!");
                return;
            }

            int id = Integer.parseInt(modelTabel.getValueAt(baris, 0).toString());
            String nama = modelTabel.getValueAt(baris, 2).toString();

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Hapus " + nama + " ?",
                    "Konfirmasi",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                cDAO.delete(id);

                KoneksiDatabase.addLog(UserSession.getIdAkun(), "Hapus Customer: " + nama);

                JOptionPane.showMessageDialog(this, "Berhasil hapus!");
                loadData();
                clear();
                setKodeOtomatis();
            }
        });
    }

    // ================= METHOD =================

    private void setKodeOtomatis() {
        txtKodeCustomer.setText(cDAO.generateKodeBaru());
    }

    private void loadData() {
        modelTabel.setRowCount(0);
        List<Customer> list = cDAO.getAll();

        for (Customer c : list) {
            modelTabel.addRow(new Object[]{
                c.getIdCustomer(),
                c.getKodeCustomer(),
                c.getNamaCustomer(),
                c.getNoTelp()
            });
        }
    }

    private void clear() {
        txtNama.setText("");
        txtNoTelp.setText("");
        tabelCustomer.clearSelection();
    }
}