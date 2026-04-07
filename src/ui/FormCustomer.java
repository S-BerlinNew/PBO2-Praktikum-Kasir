package ui;

import dao.CustomerDAO;
import model.Customer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FormCustomer extends JFrame {
    private JTextField txtKode, txtNama, txtNoTelp;
    private JTable tabelCustomer;
    private DefaultTableModel modelTabel;
    private CustomerDAO cDAO = new CustomerDAO();

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
        txtKode = new JTextField();
        panelInput.add(txtKode);

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
        JButton btnSimpan = new JButton("SIMPAN");
        JButton btnRefresh = new JButton("REFRESH");
        panelTombol.add(btnSimpan);
        panelTombol.add(btnRefresh);
        add(panelTombol, BorderLayout.SOUTH);

        // --- Event Handling ---
        loadData();

        btnRefresh.addActionListener(e -> loadData());

        btnSimpan.addActionListener(e -> {
            Customer baru = new Customer(0, txtKode.getText(), txtNama.getText(), txtNoTelp.getText());
            
            try {
                cDAO.insert(baru);//Panggil DAO
                JOptionPane.showMessageDialog(this, "Data Berhasil Disimpan!"); //Notif di UI
                loadData();//Untuk Refresh tabel
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal : " + ex.getMessage());
            }
        });
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