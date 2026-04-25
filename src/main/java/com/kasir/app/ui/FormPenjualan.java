package com.kasir.app.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import com.kasir.app.dao.*;
import com.kasir.app.model.*;
import com.kasir.app.view.*;
import com.kasir.app.view.CetakNota;

public class FormPenjualan extends JFrame {
    // Komponen
    private CustomerDAO cDAO = new CustomerDAO();
    private BarangDAO bDAO = new BarangDAO();
    private PenjualanDAO pDAO = new PenjualanDAO();
    private DetailPenjualanDAO dpDAO = new DetailPenjualanDAO();

    // Atribut Metode Pembayaran
    private String metodeTerpilih = "";

    private JTextField txtCustomer, txtHarga, txtQty, txtNoTelp, txtDiskonNota; //Tempat kasir ngetik sesuatu (Nama Customer, Jumlah Beli).
    private Akun userLogin;
    private JTextField txtStokTersedia; //untuk qty yang tersedia setiap barang
    private JComboBox<String> cbBarang; //Tempat milih barang. Biar kasir nggak capek ngetik "Raket Yonex Tipe A-123" berulang kali.
    private JTextField txtAdmin; //Nama admin
    private JTable tabelKeranjang; //Struk sementara. Tempat nampung semua barang yang mau dibeli sebelum akhirnya dicetak.
    private DefaultTableModel modelTabel; //Ini yang bertugas nambahin baris baru ke tabel saat klik tombol "Tambah".
    private JLabel labelTotal; //Cuma label penanda, biar kasir tahu kotak ini buat isi apa (misal: tulisan "Nama:").
    private double totalBelanja = 0;


    public FormPenjualan(Akun akun) {
        this.userLogin = akun; //Untuk simpan data akun yang login

        setTitle("Transaksi Penjualan Olahraga - Mode Kasir");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        

        // PANEL ATAS BAGIAN INPUT DATA
        JPanel panelAtas = new JPanel(new GridLayout(9, 2, 10, 10));
        panelAtas.setBorder(BorderFactory.createTitledBorder("Input Transaksi"));

        
        // ---- 1. Panel Khusus Customer ----
        panelAtas.add(new JLabel("Pilih/Cari Customer : "));

        JPanel panelCariCustomer = new JPanel(new BorderLayout(5, 5));
        txtCustomer = new JTextField();
        txtCustomer.setEditable(false);

        JPanel panelAksiCustomer = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JButton btnCariCustomer = new JButton("CARI");
        JButton btnTambahCustomer = new JButton("TAMBAH BARU");

        panelAksiCustomer.add(btnCariCustomer);
        panelAksiCustomer.add(btnTambahCustomer);

        panelCariCustomer.add(txtCustomer, BorderLayout.CENTER);
        panelCariCustomer.add(panelAksiCustomer, BorderLayout.EAST);

        panelAtas.add(panelCariCustomer);

        panelAtas.add (new JLabel("Nomor Telephone : "));
        txtNoTelp = new JTextField();
        txtNoTelp.setEditable(false);
        panelAtas.add(txtNoTelp);

        btnTambahCustomer.addActionListener(e -> {
            // Buka FormCustomer buat input data baru
            new FormCustomer().setVisible(true);
        });

        btnCariCustomer.addActionListener(e -> {
            JDialog d = new JDialog(this, "Cari Customer", true);
            d.setSize(500, 400);
            d.setLocationRelativeTo(this);
            d.setLayout(new BorderLayout(10, 10));

            // 1. Bagian Atas: Input Pencarian
            JPanel panelCari = new JPanel(new BorderLayout(5, 5));
            panelCari.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
            panelCari.add(new JLabel("Ketik Nama / No Telp: "), BorderLayout.WEST);
            
            JTextField txtCari = new JTextField();
            panelCari.add(txtCari, BorderLayout.CENTER);
            d.add(panelCari, BorderLayout.NORTH);

            // 2. Bagian Tengah: Tabel
            String[] kolomCust = {"ID", "Nama Customer", "No Telp"};
            DefaultTableModel modelCust = new DefaultTableModel(kolomCust, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            JTable tabelCust = new JTable(modelCust);
            
            // Sembunyikan kolom ID biar rapi
            tabelCust.getColumnModel().getColumn(0).setMinWidth(0);
            tabelCust.getColumnModel().getColumn(0).setMaxWidth(0);

            // Fungsi Load Data dengan Filter
            Runnable loadData = () -> {
                modelCust.setRowCount(0); // Bersihkan tabel
                String keyword = txtCari.getText().toLowerCase();
                List<Customer> list = cDAO.getAll(); // Ambil semua dari DB
                
                int limit = 50; // Kita batesin cuma nampilin 50 data teratas yang cocok
                int count = 0;

                for (Customer c : list) {
                    // Cek apakah nama atau no telp mengandung kata kunci
                    if (c.getNamaCustomer().toLowerCase().contains(keyword) || 
                        c.getNoTelp().contains(keyword)) {
                        
                        modelCust.addRow(new Object[]{c.getIdCustomer(), c.getNamaCustomer(), c.getNoTelp()});
                        count++; // Tambah hitungan setiap ada data yang masuk
                    }

                    // Kalau sudah mencapai limit, stop perulangan (break)
                    if (count >= limit) {
                        break; 
                    }
                }
            };

            // Jalankan load pertama kali
            loadData.run();

            // Event Ngetik langsung Filter
            txtCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { loadData.run(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { loadData.run(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { loadData.run(); }
            });

            // Pilih Data (Klik Baris)
            tabelCust.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() == 2) { // Double klik buat pilih
                        int row = tabelCust.getSelectedRow();
                        if (row != -1) {
                            txtCustomer.setText(modelCust.getValueAt(row, 1).toString());
                            txtNoTelp.setText(modelCust.getValueAt(row, 2).toString());
                            d.dispose();
                        }
                    }
                }
            });

            d.add(new JScrollPane(tabelCust), BorderLayout.CENTER);
            
            JLabel lblHint = new JLabel(" *Double klik pada baris untuk memilih customer", SwingConstants.LEFT);
            lblHint.setFont(new Font("Arial", Font.ITALIC, 10));
            d.add(lblHint, BorderLayout.SOUTH);

            d.setVisible(true);
        });


        // ---- Panel Kasir ----
        panelAtas.add(new JLabel("Nama Kasir : "));
        txtAdmin = new JTextField(userLogin.getNamaLengkap());
        txtAdmin.setEditable(false);
        txtAdmin.setBackground(new Color(235, 235, 235));
        panelAtas.add(txtAdmin);

        // ---- Panel Pilih Barang -----
        panelAtas.add(new JLabel("Pilih Barang : "));
        cbBarang = new JComboBox<>();
        panelAtas.add(cbBarang);

        panelAtas.add(new JLabel("Harga Satuan : "));
        txtHarga = new JTextField();
        txtHarga.setEditable(false); //otomatis muncul dan tidak bisa diedit
        panelAtas.add(txtHarga);

        panelAtas.add(new JLabel("Stok Tersedia : "));
        txtStokTersedia = new JTextField(); 
        txtStokTersedia.setEditable(false);
        panelAtas.add(txtStokTersedia);

        panelAtas.add(new JLabel("Jumlah Beli (Qty) : "));
        txtQty = new JTextField();
        panelAtas.add(txtQty);

        // Cukup satu kali inisialisasi
        txtDiskonNota = new JTextField("0", 10); 
        panelAtas.add(new JLabel("Diskon (%): "));
        panelAtas.add(txtDiskonNota);

        
        txtDiskonNota.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateTotalSeluruhnya(); }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateTotalSeluruhnya(); }

            public void changedUpdate(javax.swing.event.DocumentEvent e) { 
                updateTotalSeluruhnya(); }
        });
        
        add(panelAtas, BorderLayout.NORTH);


        // Panel Tengah untuk Tabel Keranjang
        String[] kolom = {"ID Barang", "Nama Barang", "Harga", "Qty","Subtotal"};
        modelTabel = new DefaultTableModel(kolom, 0);
        tabelKeranjang = new JTable(modelTabel);

        tabelKeranjang.getColumnModel().getColumn(0).setMinWidth(0);
        tabelKeranjang.getColumnModel().getColumn(0).setMaxWidth(0);

        add(new JScrollPane(tabelKeranjang), BorderLayout.CENTER);

        // Panel Samping untuk Tombol Tambah
        JPanel panelSamping = new JPanel(new GridLayout(3, 1, 5, 5)); // Pake GridLayout biar rapi atas bawah
        JButton btnTambah = new JButton("TAMBAH");
        JButton btnHapus = new JButton("HAPUS BARANG");
        JButton btnEdit = new JButton("EDIT");
        panelSamping.add(btnTambah);
        panelSamping.add(btnHapus);
        panelSamping.add(btnEdit);
        add(panelSamping, BorderLayout.EAST);

        //Untuk Edit
        tabelKeranjang.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int baris = tabelKeranjang.getSelectedRow();
                if (baris != -1) {
                    // 1. Ambil ID & Nama dari tabel
                    String idTabel = modelTabel.getValueAt(baris, 0).toString();
                    String namaTabel = modelTabel.getValueAt(baris, 1).toString();
                    String qtyTabel = modelTabel.getValueAt(baris, 3).toString();

                    // 2. Set Qty balik ke field
                    txtQty.setText(qtyTabel);

                    // 3. Cari dan set ComboBox ke barang yang sesuai
                    for (int i = 0; i < cbBarang.getItemCount(); i++) {
                        if (cbBarang.getItemAt(i).toString().startsWith(idTabel)) { 
                            cbBarang.setSelectedIndex(i);
                            break;
                        }
                    }
                    
                    // Tombol Tambah matiin, biar nggak double
                    btnTambah.setEnabled(false);
                    btnEdit.setEnabled(true);
                }
            }
        });

        

        // Panel bawah untuk Total dan Simpan
        // 1. Baris Pertama (Total & Lanjut) -> Rata Kanan
        JPanel barisSatu = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        labelTotal = new JLabel("TOTAL : Rp 0");
        labelTotal.setFont(new Font("Arial", Font.BOLD, 18));

        JButton btnLanjut = new JButton("Lanjutkan Pembayaran");
        btnLanjut.addActionListener(e -> bukaPilihanPembayaran());

        barisSatu.add(labelTotal);
        barisSatu.add(btnLanjut);

        

        // 2. Baris Kedua (Tombol Kembali) -> Rata Kiri
        JPanel barisDua = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBack = new JButton("KEMBALI");
        btnBack.setBackground(Color.DARK_GRAY);
        btnBack.setForeground(Color.WHITE);
        btnBack.addActionListener(e -> this.dispose());
        
        barisDua.add(btnBack);

        // 3. Panel Utama buat nampung baris 1 dan baris 2
        JPanel panelBawahFinal = new JPanel(new GridLayout(2, 1));
        panelBawahFinal.add(barisSatu); // Baris atas
        panelBawahFinal.add(barisDua);  // Baris bawah

        // 4. Tempel ke Frame Utama (SOUTH)
        // Pakai panelBawahFinal ya bro, jangan panelBawah lagi
        add(panelBawahFinal, BorderLayout.SOUTH);

        // Sisanya lanjutin...
        loadBarangKeCombo();


        // Memunculkan harga otomatis saat dipilih
        cbBarang.addActionListener(e -> {
            updateHarga();
        });

        // Tambah ke Tabel
        btnTambah.addActionListener(e -> {
            int qtyInput = Integer.parseInt(txtQty.getText());
            int stokAda = Integer.parseInt(txtStokTersedia.getText());

            if (qtyInput > stokAda) {
                JOptionPane.showMessageDialog(this, "Stok tidak cukup! Sisa stok: " + stokAda);
                return; // Berhenti di sini, gak jadi masuk ke tabel
            }
            tambahKeTabel();
        });

        // Hapus tabel
        btnHapus.addActionListener(e -> {
            int baris = tabelKeranjang.getSelectedRow();
            if (baris != -1) {
                modelTabel.removeRow(baris);
                updateTotalSeluruhnya(); // Update totalnya lagi
                
                // Reset state
                txtQty.setText("");
                btnTambah.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Pilih barang yang mau dihapus!");
            }
        });

        btnEdit.addActionListener(e -> {
            int baris = tabelKeranjang.getSelectedRow();
            if (baris != -1) {
                try {
                    // 1. Ambil data baru dari field qty
                    int qtyBaru = Integer.parseInt(txtQty.getText());
                    
                    // 2. Ambil harga dari kolom index ke-2 di tabel
                    double harga = Double.parseDouble(modelTabel.getValueAt(baris, 2).toString());
                    double subtotalBaru = harga * qtyBaru;

                    // 3. Update data di tabel
                    modelTabel.setValueAt(qtyBaru, baris, 3);
                    modelTabel.setValueAt(subtotalBaru, baris, 4);

                    // 4. Update total nota (panggil fungsi lo)
                    updateTotalSeluruhnya();

                    // 5. Bersihkan field & kembalikan tombol
                    tabelKeranjang.clearSelection();
                    txtQty.setText("");
                    btnTambah.setEnabled(true);
                    btnEdit.setEnabled(false);
                    
                    JOptionPane.showMessageDialog(this, "Jumlah barang berhasil diupdate!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Input Qty tidak valid!");
                }
            }
        });
    }



    // Fungsi Pendukung
    private void loadBarangKeCombo() {
        cbBarang.removeAllItems(); // Bersihkan dulu biar gak double
        cbBarang.addItem("-- Pilih Barang --");
        List<Barang> list = bDAO.getAllAktif();
        for (Barang b : list) {
            // Harus format "ID - Nama" supaya split-nya ketemu ID-nya
            cbBarang.addItem(b.getIdBarang() + " - " + b.getNamaBarang());
        }
    }


    private void updateHarga() {
        String pilihan = (String) cbBarang.getSelectedItem();
        if (pilihan == null || pilihan.equals("-- Pilih Barang --")) return;

        // Ambil ID Barang (Asumsi format: "ID - Nama")
        String id = pilihan.split(" - ")[0];
        Barang b = bDAO.getById(id); // Langsung minta ke DAO berdasarkan ID

        if (b != null) {
            txtHarga.setText(String.valueOf(b.getHargaJual()));
            txtStokTersedia.setText(String.valueOf(b.getStok())); // Tampilkan stok asli dari DB
        }
        
        if (b != null) {
            txtHarga.setText(String.valueOf(b.getHargaJual()));
        }
    }

    private void tambahKeTabel() {
        try {
            String pilihan = cbBarang.getSelectedItem().toString();
            String id = pilihan.split(" - ")[0];
            int qty = Integer.parseInt(txtQty.getText());

            Barang b = bDAO.getById(id);
            if (b != null) {
                // MASUKKAN HARGA ASLI KE TABEL
                double subtotal = b.getHargaJual() * qty;

                modelTabel.addRow(new Object[]{
                    b.getIdBarang(),
                    b.getNamaBarang(),
                    b.getHargaJual(), // Harga Normal
                    qty,
                    subtotal          // Subtotal Normal
                });

                updateTotalSeluruhnya(); // Biarkan fungsi ini yang urus diskon di akhir
                txtQty.setText("");
                cbBarang.requestFocus();
            }
            txtQty.setText("");         
            txtHarga.setText("");       
            txtStokTersedia.setText("");  
            cbBarang.setSelectedIndex(0); 
            cbBarang.requestFocus();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Pilih barang dan isi Qty dengan angka!");
        }
    }

    private void updateTotalSeluruhnya() {
        double totalKotor = 0;
        // 1. Hitung total kotor dari tabel
        for (int i = 0; i < modelTabel.getRowCount(); i++) {
            totalKotor += Double.parseDouble(modelTabel.getValueAt(i, 4).toString());
        }

        // 2. Ambil diskon persen dari textfield
        double persenDiskon = 0;
        try {
            if (!txtDiskonNota.getText().isEmpty()) {
                persenDiskon = Double.parseDouble(txtDiskonNota.getText());
            }
        } catch (Exception e) { 
            persenDiskon = 0; 
        }

        // 3. Hitung nominal diskon
        double nominalDiskon = (persenDiskon / 100) * totalKotor;

        // 4. Hitung total akhir
        totalBelanja = totalKotor - nominalDiskon;
        
        // 5. Update label
        labelTotal.setText("TOTAL : Rp " + String.format("%.0f", totalBelanja));
    }




    //Popup setelah lanjutkan pembayaran
    private void bukaPilihanPembayaran() {
        if(modelTabel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "KERANJANG KOSONG!");
            return;
        }

        String[] options = {"Tunai", "QRIS", "Transfer Bank (VA)"};
        int pilihan = JOptionPane.showOptionDialog(this, "Pilih Metode Pembayaran untuk total : Rp " + totalBelanja,
            "Metode Pemabayaran",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null, options, options[0]);

            if(pilihan != -1) {
                metodeTerpilih = options[pilihan];
                tampilkanMenungguPembayaran();
            }
    }

    //Popup meunggu komfirmasi pembayran
    private void tampilkanMenungguPembayaran() {
        JDialog dialog = new JDialog(this, "Status Pemabayaran", true);
        dialog.setLayout(new GridLayout(4, 1, 10, 10));
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);

        JLabel lblStatus = new JLabel("MENUNGGU PEMBAYARAN...", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Arial", Font.BOLD, 14));
        lblStatus.setForeground(Color.RED);

        JLabel lblMetode = new JLabel("Metode : " + metodeTerpilih, SwingConstants.CENTER);
        JLabel lblTotal = new JLabel("Total: Rp " + totalBelanja, SwingConstants.CENTER);

        JButton btnSelesai = new JButton("PEMABAYARAN SELESAI");
        btnSelesai.setBackground(new Color(34, 139, 34));
        btnSelesai.setForeground(Color.WHITE);

        btnSelesai.addActionListener(e -> {
            dialog.dispose(); //untuk tutup popup
            simpanTransaksi(); //panggil fungsi simpanTransaksi, untuk simpan ke db
        });

        dialog.add(lblStatus);
        dialog.add(lblMetode);
        dialog.add(lblTotal);
        dialog.add(btnSelesai);
        dialog.add(new JLabel("")); // Spacer

        dialog.setVisible(true);
    }


    //Fungsi untuk simpan ke db
   private void simpanTransaksi() {
        // 1. Validasi awal
        if (txtCustomer.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Customer harus diisi!");
            return;
        }
        if (modelTabel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Keranjang belanja masih kosong!");
            return;
        }
        if (txtAdmin.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Kasir Belum diisi!");
            return;
        }

        try {
            // --- STEP 1: URUS CUSTOMER DULU ---
            String namaInput = txtCustomer.getText();
    
            
            // Kita cari di database, ada gak customer dengan nama ini?
            Customer cust = cDAO.getByName(namaInput); 

            if (cust == null) {
                // Kalau namanya BELUM ADA di DB, kita buat baru otomatis
                cust = new Customer();
                cust.setNamaCustomer(namaInput);
                cust.setKodeCustomer(cDAO.generateKodeBaru());
                cust.setNoTelp(txtNoTelp.getText());

                // Simpan ke database dan ambil ID otomatisnya (Auto Increment)
                int idBaru = cDAO.insertAndGetId(cust); 

                if(idBaru == 0) {
                    JOptionPane.showMessageDialog(this, "Gagal mendapatkan ID Customer dari Database");
                    return;
                }
                cust.setIdCustomer(idBaru);
            }

            

            // --- STEP 2: SIMPAN HEADER (Ke Tabel penjualan) ---
            Penjualan p = new Penjualan();
            p.setNoNota("NOT-" + System.currentTimeMillis());
            p.setTanggal(new java.sql.Date(System.currentTimeMillis()));
            p.setCustomer(cust);

            // 1. Hitung Total Kotor
            double totalKotor = 0;
                for (int i = 0; i < modelTabel.getRowCount(); i++) {
                    totalKotor += Double.parseDouble(modelTabel.getValueAt(i, 4).toString());
                }

            // 2. Ambil Angka Persen dengan proteksi (biar gak error kalau kosong)
            double angkaPersen = 0;
            String txtDiskon = txtDiskonNota.getText().trim();
                if (!txtDiskon.isEmpty()) {
                    try {
                        angkaPersen = Double.parseDouble(txtDiskon);
                    } catch (NumberFormatException e) {
                        angkaPersen = 0;
                    }
                }

            // 3. Hitung Nominal Diskon
            double nominalDiskon = (angkaPersen / 100) * totalKotor; 

            // 4. Set ke Objek
            p.setDiskon((int) nominalDiskon); 
            p.setTotalBayar(totalBelanja); 
            p.setNamaKasir(txtAdmin.getText()); 
            p.setMetodePembayaran(metodeTerpilih);
            System.out.println("LOG: Mengirim diskon ke nota sebesar " + nominalDiskon);

            // 5. Eksekusi Simpan
            int idPenjualanBaru = pDAO.InsertAndGetId(p);
                if(idPenjualanBaru == 0) {
                    JOptionPane.showMessageDialog(this, "Gagal simpan header penjualan! Cek koneksi database.");
                    return;
                }


            // --- STEP 3: SIMPAN DETAIL ---
            List<DetailPenjualan> listUntukNota = new ArrayList<>();

            for (int i = 0; i < modelTabel.getRowCount(); i++) {
                DetailPenjualan dp = new DetailPenjualan();
                dp.setIdPenjualan(idPenjualanBaru); 
                
                Barang b = new Barang();
                b.setIdBarang(modelTabel.getValueAt(i, 0).toString());
                b.setNamaBarang(modelTabel.getValueAt(i, 1).toString());
                b.setHargaJual(Double.parseDouble(modelTabel.getValueAt(i, 2).toString()));
                
                dp.setBarang(b);
                
                int qty = Integer.parseInt(modelTabel.getValueAt(i, 3).toString());
                dp.setQty(qty);
                
                double subtotal = Double.parseDouble(modelTabel.getValueAt(i, 4).toString());
                dp.setSubtotal(subtotal);

                dpDAO.insert(dp); 
                bDAO.kurangiStok(b.getIdBarang(), qty);

                listUntukNota.add(dp);
            }

            // --- STEP 4: FINISHING ---
            JOptionPane.showMessageDialog(this, "Transaksi Berhasil Disimpan!");
            CetakNota nota = new CetakNota(this, true, p, listUntukNota);
            nota.setVisible(true);
            
            // Reset form
            modelTabel.setRowCount(0);
            txtCustomer.setText("");
            txtQty.setText("");
            txtNoTelp.setText("");
            labelTotal.setText("TOTAL : Rp 0");
            totalBelanja = 0;
            cbBarang.setSelectedIndex(0);
        

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal Simpan: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Cara jalanin GUI biar stabil
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                Akun akunTesting = new Akun(99, "tester", "123", "Kasir Testing", "Kasir");

                new FormPenjualan(akunTesting).setVisible(true);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        });
    }
}