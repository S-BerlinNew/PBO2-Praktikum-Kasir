package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import dao.BarangDAO;
import dao.CustomerDAO;
import model.Barang;
import model.DetailPenjualan;
import model.Penjualan;
import view.CetakNota;

public class FormPenjualan extends JFrame {
    // Komponen
    private CustomerDAO cDAO = new CustomerDAO();
    private BarangDAO bDAO = new BarangDAO();
    private dao.PenjualanDAO pDAO = new dao.PenjualanDAO();
    private dao.DetailPenjualanDAO dpDAO = new dao.DetailPenjualanDAO();

    // Atribut Metode Pembayaran
    private String metodeTerpilih = "";

    private JTextField txtCustomer, txtHarga, txtQty, txtNoTelp; //Tempat kasir ngetik sesuatu (Nama Customer, Jumlah Beli).
    private JTextField txtStokTersedia; //untuk qty yang tersedia setiap barang
    private JComboBox<String> cbBarang; //Tempat milih barang. Biar kasir nggak capek ngetik "Raket Yonex Tipe A-123" berulang kali.
    private JTextField txtAdmin; //Nama admin
    private JTable tabelKeranjang; //Struk sementara. Tempat nampung semua barang yang mau dibeli sebelum akhirnya dicetak.
    private DefaultTableModel modelTabel; //Ini yang bertugas nambahin baris baru ke tabel saat klik tombol "Tambah".
    private JLabel labelTotal; //Cuma label penanda, biar kasir tahu kotak ini buat isi apa (misal: tulisan "Nama:").
    private double totalBelanja = 0;


    public FormPenjualan() {
        setTitle("Transaksi Penjualan Olahraga - Mode Kasir");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // PANEL ATAS BAGIAN INPUT DATA
        JPanel panelAtas = new JPanel(new GridLayout(7, 2, 10, 10));
        panelAtas.setBorder(BorderFactory.createTitledBorder("Input Transaksi"));

        panelAtas.add(new JLabel("Nama Customer : "));
        txtCustomer = new JTextField();
        panelAtas.add(txtCustomer);

        panelAtas.add(new JLabel("Nama Kasir : "));
        txtAdmin = new JTextField();
        panelAtas.add(txtAdmin);

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

        panelAtas.add(new JLabel("Nomor Telephone : "));
        txtNoTelp = new JTextField();
        panelAtas.add(txtNoTelp);

        add(panelAtas, BorderLayout.NORTH);



        // Panel Tengah untuk Tabel Keranjang
        String[] kolom = {"ID Barang", "Nama Barang", "Harga", "Qty", "Dsikon (%)","Subtotal", "No Telp"};
        modelTabel = new DefaultTableModel(kolom, 0);
        tabelKeranjang = new JTable(modelTabel);

        tabelKeranjang.getColumnModel().getColumn(0).setMinWidth(0);
        tabelKeranjang.getColumnModel().getColumn(0).setMaxWidth(0);

        add(new JScrollPane(tabelKeranjang), BorderLayout.CENTER);

        // Panel Samping untuk Tombol Tambah
        JPanel panelSamping = new JPanel(new GridLayout(2, 1, 5, 5)); // Pake GridLayout biar rapi atas bawah
        JButton btnTambah = new JButton("TAMBAH");
        JButton btnHapus = new JButton("HAPUS BARANG");
        panelSamping.add(btnTambah);
        panelSamping.add(btnHapus);
        add(panelSamping, BorderLayout.EAST);

        

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
                // Pakai Double.parseDouble dan toString() biar gak kena ClassCastException
                double sub = Double.parseDouble(modelTabel.getValueAt(baris, 5).toString());
                totalBelanja -= sub;
                labelTotal.setText("TOTAL : Rp " + String.format("%.0f", totalBelanja));
                modelTabel.removeRow(baris);
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
            String id = pilihan.split(" - ")[0]; // Ambil ID dari teks combo
            int qty = Integer.parseInt(txtQty.getText());
            String noTelp = txtNoTelp.getText();
            String namaAdmin = txtAdmin.getText();

            Barang b = bDAO.getById(id);
            if (b != null) {
                DetailPenjualan detail = new DetailPenjualan(0, 0, b, qty);

                // Masukkan 6 data sesuai array 'kolom'
                modelTabel.addRow(new Object[]{
                    b.getIdBarang(),    // Kolom 0 (ID - Tersembunyi)
                    b.getNamaBarang(),  // Kolom 1
                    b.getHargaJual(),    // Kolom 2
                    qty,                // Kolom 3
                    b.getDiskon() + "%",// Kolom 4
                    detail.getSubtotal(),// Kolom 5
                    noTelp,
                    namaAdmin
                });

                totalBelanja += detail.getSubtotal();
                labelTotal.setText("TOTAL : Rp " + String.format("%.0f", totalBelanja));
                txtQty.setText("");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Pilih barang dan isi Qty!");
        }
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
        if (txtCustomer.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Kasir Belum diisi!");
            return;
        }

        try {
            // --- STEP 1: URUS CUSTOMER DULU ---
            String namaInput = txtCustomer.getText();
    
            
            // Kita cari di database, ada gak customer dengan nama ini?
            model.Customer cust = cDAO.getByName(namaInput); 

            if (cust == null) {
                // Kalau namanya BELUM ADA di DB, kita buat baru otomatis
                cust = new model.Customer();
                cust.setNamaCustomer(namaInput);
                cust.setKodeCustomer("CUST-" + System.currentTimeMillis());
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
            p.setTanggal(new java.sql.Date(System.currentTimeMillis())); // Pakai sql date biar aman ke DB
            p.setCustomer(cust); // Pakai objek cust hasil pencarian/pembuatan di atas
            p.setTotalBayar(totalBelanja);
            p.setNamaKasir(txtAdmin.getText()); 
            p.setMetodePembayaran(metodeTerpilih);
            

            int idPenjualanBaru = pDAO.InsertAndGetId(p);

            if(idPenjualanBaru == 0) {
                JOptionPane.showMessageDialog(this, "Gagal simpan header penjualan!");
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
                
                double subtotal = Double.parseDouble(modelTabel.getValueAt(i, 5).toString());
                dp.setSubtotal(subtotal);

                dpDAO.insert(dp); 
                bDAO.kurangiStok(b.getIdBarang(), qty);

                listUntukNota.add(dp);
            }

            // --- STEP 4: FINISHING ---
            JOptionPane.showMessageDialog(this, "Transaksi Berhasil Disimpan!");
            view.CetakNota nota = new view.CetakNota(this, true, p, listUntukNota);
            nota.setVisible(true);
            
            // Reset form
            modelTabel.setRowCount(0);
            txtCustomer.setText("");
            txtQty.setText("");
            txtNoTelp.setText("");
            labelTotal.setText("TOTAL : Rp 0");
            totalBelanja = 0;
            cbBarang.setSelectedIndex(0);
            txtAdmin.setText("");
        

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal Simpan: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Cara jalanin GUI biar stabil
        SwingUtilities.invokeLater(() -> {
            new FormPenjualan().setVisible(true);
        });
    }
}