package ui;

import dao.CustomerDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Customer;
import model.UserSession;

public class FormCustomer extends JPanel {

    private JTextField txtNama, txtNoTelp, txtKodeCustomer;
    private JTable tabelCustomer;
    private DefaultTableModel modelTabel;
    private CustomerDAO cDAO = new CustomerDAO();
    private JButton btnEdit, btnSimpan, btnRefresh, btnHapus;

    public FormCustomer() {

        setLayout(new BorderLayout(15,15));
        setBackground(new Color(245,247,250));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // ================= TITLE =================
        JLabel title = new JLabel("DATA CUSTOMER");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(44,62,80));

        // ================= PANEL INPUT =================
        JPanel panelInput = new JPanel(new GridLayout(3,2,10,10));
        panelInput.setBackground(Color.WHITE);
        panelInput.setBorder(BorderFactory.createTitledBorder("Input Customer"));

        txtKodeCustomer = new JTextField();
        txtKodeCustomer.setEditable(false);
        txtKodeCustomer.setBackground(new Color(235,235,235));

        txtNama = new JTextField();
        txtNoTelp = new JTextField();

        panelInput.add(new JLabel("Kode Customer:"));
        panelInput.add(txtKodeCustomer);
        panelInput.add(new JLabel("Nama Pelanggan:"));
        panelInput.add(txtNama);
        panelInput.add(new JLabel("No. Telepon:"));
        panelInput.add(txtNoTelp);

        // ================= TABEL =================
        String[] kolom = {"ID", "Kode", "Nama", "No. Telp"};
        modelTabel = new DefaultTableModel(kolom, 0);
        tabelCustomer = new JTable(modelTabel);

        JScrollPane scroll = new JScrollPane(tabelCustomer);

        // ================= BUTTON =================
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelTombol.setBackground(new Color(245,247,250));

        btnSimpan = new JButton("SIMPAN");
        btnRefresh = new JButton("REFRESH");
        btnEdit = new JButton("UPDATE");
        btnHapus = new JButton("HAPUS");

        panelTombol.add(btnSimpan);
        panelTombol.add(btnRefresh);
        panelTombol.add(btnEdit);
        panelTombol.add(btnHapus);

        // ================= ROLE =================
        String roleSekarang = UserSession.getRole();

        if ("kasir".equalsIgnoreCase(roleSekarang)) {
            btnEdit.setVisible(false);
            btnHapus.setVisible(false);
        }

        // ================= WRAPPER =================
        JPanel atas = new JPanel(new BorderLayout(10,10));
        atas.setOpaque(false);
        atas.add(title, BorderLayout.NORTH);
        atas.add(panelInput, BorderLayout.CENTER);

        add(atas, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelTombol, BorderLayout.SOUTH);

        // ================= EVENT =================
        loadData();
        setKodeOtomatis();

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
            clearFields();
            setKodeOtomatis();
        });

        btnSimpan.addActionListener(e -> {
            Customer c = new Customer();
            c.setKodeCustomer(txtKodeCustomer.getText());
            c.setNamaCustomer(txtNama.getText());
            c.setNoTelp(txtNoTelp.getText());

            cDAO.insert(c);

            JOptionPane.showMessageDialog(this, "Customer Berhasil Ditambah!");
            loadData();
            clearFields();
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

            JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
            loadData();
            clearFields();
            setKodeOtomatis();
        });

        btnHapus.addActionListener(e -> {
            int baris = tabelCustomer.getSelectedRow();
            if (baris == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data dulu!");
                return;
            }

            int id = Integer.parseInt(modelTabel.getValueAt(baris, 0).toString());

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Yakin mau hapus?", "Konfirmasi", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                cDAO.delete(id);
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                loadData();
                clearFields();
                setKodeOtomatis();
            }
        });
    }

    private void setKodeOtomatis() {
        String kodeBaru = cDAO.generateKodeBaru();
        txtKodeCustomer.setText(kodeBaru);
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

    private void clearFields() {
        txtNama.setText("");
        txtNoTelp.setText("");
        tabelCustomer.clearSelection();
    }
}