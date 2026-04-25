package ui;

import dao.BarangDAO;
import dao.CustomerDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Barang;

public class FormPenjualan extends JPanel {

    private CustomerDAO cDAO = new CustomerDAO();
    private BarangDAO bDAO = new BarangDAO();
    private dao.PenjualanDAO pDAO = new dao.PenjualanDAO();
    private dao.DetailPenjualanDAO dpDAO = new dao.DetailPenjualanDAO();

    private String metodeTerpilih = "";

    private JTextField txtCustomer, txtHarga, txtQty, txtNoTelp, txtDiskonNota;
    private JTextField txtStokTersedia;
    private JComboBox<String> cbBarang;
    private JTextField txtAdmin;

    private JTable tabelKeranjang;
    private DefaultTableModel modelTabel;

    private JLabel labelTotal;
    private double totalBelanja = 0;

    private model.Akun userLogin;

    public FormPenjualan(model.Akun akun) {
        this.userLogin = akun;

        setLayout(new BorderLayout(10,10));

        // ================= PANEL ATAS =================
        JPanel panelAtas = new JPanel(new GridLayout(9,2,10,10));
        panelAtas.setBorder(BorderFactory.createTitledBorder("Input Transaksi"));

        panelAtas.add(new JLabel("Pilih/Cari Customer : "));
        txtCustomer = new JTextField();
        txtCustomer.setEditable(false);

        JButton btnCariCustomer = new JButton("CARI");
        JButton btnTambahCustomer = new JButton("TAMBAH BARU");

        JPanel custPanel = new JPanel(new BorderLayout());
        custPanel.add(txtCustomer, BorderLayout.CENTER);

        JPanel aksi = new JPanel(new FlowLayout(FlowLayout.LEFT));
        aksi.add(btnCariCustomer);
        aksi.add(btnTambahCustomer);

        custPanel.add(aksi, BorderLayout.EAST);
        panelAtas.add(custPanel);

        panelAtas.add(new JLabel("Nomor Telephone : "));
        txtNoTelp = new JTextField();
        txtNoTelp.setEditable(false);
        panelAtas.add(txtNoTelp);

        panelAtas.add(new JLabel("Nama Kasir : "));
        txtAdmin = new JTextField(userLogin.getNamaLengkap());
        txtAdmin.setEditable(false);
        panelAtas.add(txtAdmin);

        panelAtas.add(new JLabel("Pilih Barang : "));
        cbBarang = new JComboBox<>();
        panelAtas.add(cbBarang);

        panelAtas.add(new JLabel("Harga Satuan : "));
        txtHarga = new JTextField();
        txtHarga.setEditable(false);
        panelAtas.add(txtHarga);

        panelAtas.add(new JLabel("Stok Tersedia : "));
        txtStokTersedia = new JTextField();
        txtStokTersedia.setEditable(false);
        panelAtas.add(txtStokTersedia);

        panelAtas.add(new JLabel("Jumlah Beli (Qty) : "));
        txtQty = new JTextField();
        panelAtas.add(txtQty);

        txtDiskonNota = new JTextField("0");
        panelAtas.add(new JLabel("Diskon (%): "));
        panelAtas.add(txtDiskonNota);

        add(panelAtas, BorderLayout.NORTH);

        // ================= TABEL =================
        String[] kolom = {"ID Barang", "Nama Barang", "Harga", "Qty","Subtotal"};
        modelTabel = new DefaultTableModel(kolom, 0);
        tabelKeranjang = new JTable(modelTabel);

        add(new JScrollPane(tabelKeranjang), BorderLayout.CENTER);

        // ================= KANAN =================
        JPanel kanan = new JPanel(new GridLayout(2,1));
        JButton btnTambah = new JButton("TAMBAH");
        JButton btnHapus = new JButton("HAPUS");

        kanan.add(btnTambah);
        kanan.add(btnHapus);

        add(kanan, BorderLayout.EAST);

        // ================= BAWAH =================
        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        labelTotal = new JLabel("TOTAL : Rp 0");

        JButton btnBayar = new JButton("LANJUTKAN PEMBAYARAN");

        bawah.add(labelTotal);
        bawah.add(btnBayar);

        add(bawah, BorderLayout.SOUTH);

        // ================= EVENT =================
        loadBarangKeCombo();

        cbBarang.addActionListener(e -> updateHarga());

        btnTambah.addActionListener(e -> tambahKeTabel());

        btnHapus.addActionListener(e -> {
            int row = tabelKeranjang.getSelectedRow();
            if(row != -1){
                modelTabel.removeRow(row);
                updateTotalSeluruhnya();
            }
        });

        txtDiskonNota.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e){ updateTotalSeluruhnya(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e){ updateTotalSeluruhnya(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e){ updateTotalSeluruhnya(); }
        });

        btnBayar.addActionListener(e -> bukaPilihanPembayaran());
    }

    // ================= LOGIC ASLI KAMU =================

    private void loadBarangKeCombo() {
        cbBarang.removeAllItems();
        cbBarang.addItem("-- Pilih Barang --");
        List<Barang> list = bDAO.getAllAktif();
        for (Barang b : list) {
            cbBarang.addItem(b.getIdBarang() + " - " + b.getNamaBarang());
        }
    }

    private void updateHarga() {
        String pilihan = (String) cbBarang.getSelectedItem();
        if (pilihan == null || pilihan.equals("-- Pilih Barang --")) return;

        String id = pilihan.split(" - ")[0];
        Barang b = bDAO.getById(id);

        if (b != null) {
            txtHarga.setText(String.valueOf(b.getHargaJual()));
            txtStokTersedia.setText(String.valueOf(b.getStok()));
        }
    }

    private void tambahKeTabel() {
        try {
            String pilihan = cbBarang.getSelectedItem().toString();
            String id = pilihan.split(" - ")[0];
            int qty = Integer.parseInt(txtQty.getText());

            Barang b = bDAO.getById(id);

            if (b != null) {
                double subtotal = b.getHargaJual() * qty;

                modelTabel.addRow(new Object[]{
                        b.getIdBarang(),
                        b.getNamaBarang(),
                        b.getHargaJual(),
                        qty,
                        subtotal
                });

                updateTotalSeluruhnya();
                txtQty.setText("");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Input salah!");
        }
    }

    private void updateTotalSeluruhnya() {
        double total = 0;

        for (int i = 0; i < modelTabel.getRowCount(); i++) {
            total += Double.parseDouble(modelTabel.getValueAt(i, 4).toString());
        }

        totalBelanja = total;
        labelTotal.setText("TOTAL : Rp " + totalBelanja);
    }

    private void bukaPilihanPembayaran() {
        String[] options = {"Tunai", "QRIS", "Transfer"};
        int pilihan = JOptionPane.showOptionDialog(this, "Pilih metode pembayaran",
                "Pembayaran", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (pilihan != -1) {
            metodeTerpilih = options[pilihan];
            JOptionPane.showMessageDialog(this, "Metode: " + metodeTerpilih);
        }
    }
}