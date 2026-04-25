```java
package com.kasir.app.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

import com.kasir.app.dao.*;
import com.kasir.app.model.*;
import com.kasir.app.view.CetakNota;

public class FormPenjualan extends JFrame {

    private CustomerDAO cDAO = new CustomerDAO();
    private BarangDAO bDAO = new BarangDAO();
    private PenjualanDAO pDAO = new PenjualanDAO();
    private DetailPenjualanDAO dpDAO = new DetailPenjualanDAO();

    private String metodeTerpilih = "";

    private JTextField txtCustomer, txtHarga, txtQty, txtNoTelp, txtDiskonNota;
    private JTextField txtStokTersedia, txtAdmin;
    private JComboBox<String> cbBarang;

    private JTable tabelKeranjang;
    private DefaultTableModel modelTabel;

    private JLabel labelTotal;
    private double totalBelanja = 0;

    private Akun userLogin;

    public FormPenjualan(Akun akun) {
        this.userLogin = akun;

        setTitle("Transaksi Penjualan");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));

        // ===== PANEL ATAS =====
        JPanel panelAtas = new JPanel(new GridLayout(8,2,10,10));
        panelAtas.setBorder(BorderFactory.createTitledBorder("Input Transaksi"));

        txtCustomer = new JTextField(); txtCustomer.setEditable(false);
        txtNoTelp = new JTextField(); txtNoTelp.setEditable(false);

        JButton btnCari = new JButton("CARI");
        btnCari.addActionListener(e -> pilihCustomer());

        panelAtas.add(new JLabel("Customer"));
        panelAtas.add(btnCari);
        panelAtas.add(new JLabel("Nama"));
        panelAtas.add(txtCustomer);
        panelAtas.add(new JLabel("No Telp"));
        panelAtas.add(txtNoTelp);

        txtAdmin = new JTextField(userLogin.getNamaLengkap());
        txtAdmin.setEditable(false);
        panelAtas.add(new JLabel("Kasir"));
        panelAtas.add(txtAdmin);

        cbBarang = new JComboBox<>();
        panelAtas.add(new JLabel("Barang"));
        panelAtas.add(cbBarang);

        txtHarga = new JTextField(); txtHarga.setEditable(false);
        panelAtas.add(new JLabel("Harga"));
        panelAtas.add(txtHarga);

        txtStokTersedia = new JTextField(); txtStokTersedia.setEditable(false);
        panelAtas.add(new JLabel("Stok"));
        panelAtas.add(txtStokTersedia);

        txtQty = new JTextField();
        panelAtas.add(new JLabel("Qty"));
        panelAtas.add(txtQty);

        txtDiskonNota = new JTextField("0");
        panelAtas.add(new JLabel("Diskon (%)"));
        panelAtas.add(txtDiskonNota);

        add(panelAtas, BorderLayout.NORTH);

        // ===== TABEL =====
        String[] kolom = {"ID","Nama","Harga","Qty","Subtotal"};
        modelTabel = new DefaultTableModel(kolom,0);
        tabelKeranjang = new JTable(modelTabel);
        add(new JScrollPane(tabelKeranjang), BorderLayout.CENTER);

        // ===== SAMPING =====
        JPanel kanan = new JPanel(new GridLayout(3,1,10,10));
        JButton btnTambah = new JButton("Tambah");
        JButton btnHapus = new JButton("Hapus");
        JButton btnEdit = new JButton("Edit");

        kanan.add(btnTambah);
        kanan.add(btnHapus);
        kanan.add(btnEdit);
        add(kanan, BorderLayout.EAST);

        // ===== BAWAH =====
        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        labelTotal = new JLabel("TOTAL: 0");
        JButton btnBayar = new JButton("Bayar");

        bawah.add(labelTotal);
        bawah.add(btnBayar);
        add(bawah, BorderLayout.SOUTH);

        // ===== EVENT =====
        cbBarang.addActionListener(e -> updateHarga());

        btnTambah.addActionListener(e -> tambahKeTabel());
        btnHapus.addActionListener(e -> hapusItem());
        btnEdit.addActionListener(e -> editItem());

        btnBayar.addActionListener(e -> bukaPembayaran());

        loadBarang();
    }

    // ===== CUSTOMER =====
    private void pilihCustomer(){
        List<Customer> list = cDAO.getAll();
        String[] nama = list.stream().map(Customer::getNamaCustomer).toArray(String[]::new);

        String pilih = (String) JOptionPane.showInputDialog(this,"Pilih Customer","",JOptionPane.PLAIN_MESSAGE,null,nama,nama[0]);

        if(pilih!=null){
            Customer c = cDAO.getByName(pilih);
            txtCustomer.setText(c.getNamaCustomer());
            txtNoTelp.setText(c.getNoTelp());
        }
    }

    // ===== BARANG =====
    private void loadBarang(){
        cbBarang.removeAllItems();
        for(Barang b: bDAO.getAllAktif()){
            cbBarang.addItem(b.getIdBarang()+" - "+b.getNamaBarang());
        }
    }

    private void updateHarga(){
        if(cbBarang.getSelectedItem()==null) return;
        String id = cbBarang.getSelectedItem().toString().split(" - ")[0];
        Barang b = bDAO.getById(id);

        txtHarga.setText(String.valueOf(b.getHargaJual()));
        txtStokTersedia.setText(String.valueOf(b.getStok()));
    }

    // ===== KERANJANG =====
    private void tambahKeTabel(){
        try{
            String id = cbBarang.getSelectedItem().toString().split(" - ")[0];
            Barang b = bDAO.getById(id);
            int qty = Integer.parseInt(txtQty.getText());

            double subtotal = b.getHargaJual()*qty;

            modelTabel.addRow(new Object[]{
                b.getIdBarang(),
                b.getNamaBarang(),
                b.getHargaJual(),
                qty,
                subtotal
            });

            hitungTotal();

        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"Input salah!");
        }
    }

    private void hapusItem(){
        int row = tabelKeranjang.getSelectedRow();
        if(row!=-1){
            modelTabel.removeRow(row);
            hitungTotal();
        }
    }

    private void editItem(){
        int row = tabelKeranjang.getSelectedRow();
        if(row!=-1){
            int qty = Integer.parseInt(txtQty.getText());
            double harga = Double.parseDouble(modelTabel.getValueAt(row,2).toString());
            modelTabel.setValueAt(qty,row,3);
            modelTabel.setValueAt(harga*qty,row,4);
            hitungTotal();
        }
    }

    private void hitungTotal(){
        totalBelanja = 0;
        for(int i=0;i<modelTabel.getRowCount();i++){
            totalBelanja += Double.parseDouble(modelTabel.getValueAt(i,4).toString());
        }
        labelTotal.setText("TOTAL: "+totalBelanja);
    }

    // ===== PEMBAYARAN =====
    private void bukaPembayaran(){
        String[] opsi = {"Tunai","QRIS","Transfer"};
        int pilih = JOptionPane.showOptionDialog(this,"Pilih metode","",0,0,null,opsi,opsi[0]);

        if(pilih!=-1){
            metodeTerpilih = opsi[pilih];
            simpanTransaksi();
        }
    }

    private void simpanTransaksi(){
        JOptionPane.showMessageDialog(this,"Transaksi berhasil!");
        modelTabel.setRowCount(0);
        hitungTotal();
    }
}
```
