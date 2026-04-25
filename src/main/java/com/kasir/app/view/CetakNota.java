package com.kasir.app.view;

import com.kasir.app.model.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.FontFactory;
import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.util.List;

public class CetakNota extends JDialog {
    private JTextArea txtPreview;
    private JButton btnSave, btnClose;
    private Penjualan p;
    private List<DetailPenjualan> listDetail;

    public CetakNota(Frame parent, boolean model, Penjualan p, List<DetailPenjualan>listDetail) {
        super(parent, model);
        if (p == null) {
            JOptionPane.showMessageDialog(parent, "Data Penjualan Kosong!");
            this.dispose();
            return;
        }
        this.p = p;
        this.listDetail = listDetail;

        setTitle("Nota Penjualan - " + p.getNoNota());
        setSize(410, 600);
        setLocationRelativeTo(parent);

        initLayout();
        generatePreview();
    }


    private void initLayout() {
        // TODO Auto-generated method stub
        txtPreview = new JTextArea();
        txtPreview.setEditable(false);
        
        txtPreview.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));
        txtPreview.setBackground(new Color(250,250,250));

        btnSave = new JButton("Save as PDF");
        btnClose = new JButton("Close");

        setLayout(new BorderLayout());
        add(new JScrollPane(txtPreview), BorderLayout.CENTER);

        JPanel pnlTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlTombol.add(btnSave);
        pnlTombol.add(btnClose);
        add(pnlTombol, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> saveToPDF());
        btnClose.addActionListener(e -> dispose());
    }

    private String centerText(String text, int width) {
        if (text.length() >= width) return text;
        int spaces = (width - text.length()) / 2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < spaces; i++) {
            sb.append(" ");
        }
        sb.append(text);
        return sb.toString();
    }

    private void generatePreview() {
        int lebarNota = 45;
        StringBuilder sb = new StringBuilder();

        // --- HEADER ---
        sb.append(centerText("Toko Olahraga Amatir", lebarNota)).append("\n");
        sb.append(centerText("Jl. Purnama 2, Kota Pontianak", lebarNota)).append("\n");
        sb.append(centerText("Kalimantan Barat", lebarNota)).append("\n");
        sb.append("-------------------------------------------------\n");
        sb.append(String.format("No Nota  : %s\n", p.getNoNota()));
        sb.append(String.format("Kasir    : %s\n", p.getNamaKasir()));
        sb.append(String.format("Tanggal  : %s\n", new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm").format(p.getTanggal())));
        
        String namaCust = (p.getCustomer() != null) ? p.getCustomer().getNamaCustomer() : "Umum";
        sb.append(String.format("Customer : %s\n", namaCust));
        sb.append("-------------------------------------------------\n");
        sb.append(String.format("%-20s %-3s %-10s %-12s", "Barang", "Qty", "Harga", "Subtotal"));
        sb.append("\n-------------------------------------------------\n");

        // --- ISI BARANG & HITUNG TOTAL KOTOR ---
        double totalKotor = 0;
        if (listDetail != null) {
            for(DetailPenjualan d : listDetail) {
                String nama = d.getBarang().getNamaBarang();
                if(nama.length() > 18 ) nama = nama.substring(0, 15) + "...";

                sb.append(String.format("%-20s %-3d %,10.0f %,12.0f\n",
                        nama, d.getQty(), d.getBarang().getHargaJual(), d.getSubtotal()
                ));
                totalKotor += d.getSubtotal(); // Sekalian hitung di sini
            }
        }

        sb.append("-------------------------------------------------\n");

        // --- LOGIKA DISKON ---
        double nominalDiskon = p.getDiskon(); 
        if (nominalDiskon > 0) {
            double persenDiskon = (totalKotor > 0) ? (nominalDiskon / totalKotor) * 100 : 0;
            sb.append(String.format("Total Kotor     : Rp %,15.0f\n", totalKotor));
            // Gunakan %.0f agar persen tidak ada koma nol (misal 10%)
            sb.append(String.format("Diskon (%.0f%%)    : Rp %,15.0f\n", persenDiskon, nominalDiskon));
            sb.append("-------------------------------------------------\n");
        }

        // --- TOTAL BAYAR ---
        sb.append(String.format("TOTAL BAYAR     : Rp %,15.0f\n", p.getTotalBayar()));
        sb.append("-------------------------------------------------\n\n");
        
        // --- FOOTER ---
        sb.append(centerText("Terima Kasih Atas Kunjungan Anda!", lebarNota)).append("\n");
        sb.append(centerText("-- Closed Bill --", lebarNota)).append("\n");
        
        txtPreview.setText(sb.toString());
    }
    

    private void saveToPDF() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Pilih Tempat Simpan Nota");
        // PERBAIKAN: Hilangkan tanda '+' yang tidak perlu di nama file
        chooser.setSelectedFile(new java.io.File("Nota_" + p.getNoNota() + ".pdf"));

        if(chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            Document document = new Document();
            try {
                PdfWriter.getInstance(document, new FileOutputStream(path));
                document.open();

                com.itextpdf.text.Font font = FontFactory.getFont(FontFactory.COURIER, 10);

                String[] lines = txtPreview.getText().split("\n");
                for(String line : lines) {
                    Paragraph pPara = new Paragraph(line, font);
                    pPara.setSpacingAfter(0);
                    pPara.setSpacingBefore(0);
                    document.add(pPara);
                }

                document.close();
                JOptionPane.showMessageDialog(this, "Nota Berhasil Disimpan ke PDF\nLokasi: " + path);
                this.dispose();
            } catch(Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error Saat Membuat PDF: " + e.getMessage());
            }
        }
    }

    // UNTUK TEST TAMPILAN
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            // 1. Siapkan Customer
            Customer cust = new Customer(1, "Budi Santoso", "0812345", "Pontianak");

            // 2. Siapkan List Belanjaan (Harganya Harga Normal)
            java.util.List<DetailPenjualan> listBelanja = new java.util.ArrayList<>();

            // BARANG 1
            Barang b1 = new Barang();
            b1.setIdBarang("B001");
            b1.setNamaBarang("Raket Yonex Nano");
            b1.setHargaJual(500000); // Harga Asli
            
            // DetailPenjualan: ID, ID_Penjualan, ObjekBarang, Qty, Subtotal
            DetailPenjualan d1 = new DetailPenjualan(1, 0, b1, 2, 1000000);
            listBelanja.add(d1);

            // BARANG 2
            Barang b2 = new Barang();
            b2.setIdBarang("B002");
            b2.setNamaBarang("Bola Kasti");
            b2.setHargaJual(20000); // Harga Asli
            
            DetailPenjualan d2 = new DetailPenjualan(2, 0, b2, 5, 1000000);
            listBelanja.add(d2);

            // 3. Siapkan Header Penjualan
            Penjualan p = new Penjualan();
            p.setNoNota("NOTA-TEST-001");
            p.setTanggal(new java.sql.Date(System.currentTimeMillis()));
            p.setCustomer(cust);
            p.setNamaKasir("Admin Amatir");
            p.setListDetail(listBelanja);
            
            // --- LOGIKA DISKON NOTA DI SINI ---
            int persenDiskon = 10; // Contoh diskon 10%
            double totalKotor = 1100000; // (2*500rb + 5*20rb)
            
            // Hitung nominal diskonnya
            double nominalDiskon = (double) persenDiskon / 100 * totalKotor;
            
            // Set ke objek penjualan (Sesuai model lo yang 'int')
            p.setDiskon((int) nominalDiskon); 
            
            // Set Total Bayar (Total Kotor - Nominal Diskon)
            p.setTotalBayar(totalKotor - nominalDiskon);

            // 4. Tampilkan Dialog
            new CetakNota(null, true, p, listBelanja).setVisible(true);
        });
    }
}
