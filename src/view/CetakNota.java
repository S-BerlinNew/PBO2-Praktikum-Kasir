package view;

import model.Penjualan;
import model.DetailPenjualan;
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

        sb.append(centerText("Toko Olahraga Amatir", lebarNota)).append("\n");
        sb.append(centerText("Toko Olahraga Pontianak Jl. Purnama 2", lebarNota)).append("\n");
        sb.append(centerText("Kota Pontianak, Kalimantan Barat", lebarNota)).append("\n");
        sb.append("-------------------------------------------------\n");
        sb.append("No Nota : ").append(p.getNoNota()).append("\n");
        sb.append("Kasir : ").append(p.getNamaKasir()).append("\n");
        sb.append("Tanggal : ").append(new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm").format(p.getTanggal())).append("\n");
        sb.append("Customer : ").append(p.getCustomer().getNamaCustomer()).append("\n");
        sb.append("-------------------------------------------------\n");
        sb.append(String.format("%-20s %-3s %-10s %-10s", "Barang", "Qty", "Harga", "Subtotal"));
        sb.append("\n-------------------------------------------------\n");

        for(DetailPenjualan d : listDetail) {
            String nama = d.getBarang().getNamaBarang();
            if(nama.length() > 18 ) nama = nama.substring(0, 15) + "...";

            sb.append(String.format("%-20s %-3d %-10.0f %-10.0f\n",
                    nama, d.getQty(), d.getBarang().getHargaJual(), d.getSubtotal()
            ));
        }

        sb.append("-------------------------------------------------\n");
        sb.append(String.format("TOTAL BAYAR     : Rp %,10.0f\n", p.getTotalBayar()));
        sb.append("-------------------------------------------------\n\n");
        sb.append(centerText("      Terima Kasih Atas Kunjungan Anda!     ",lebarNota)).append("\n");
        sb.append(centerText("--Closed Bill--", lebarNota)).append("\n");
        
        txtPreview.setText(sb.toString());
    }
    

    private void saveToPDF() {
        // TODO Auto-generated method stub
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Pilih Tempat Simpan Nota");
        chooser.setSelectedFile(new java.io.File("Nota - " + p.getNoNota() + "+.pdf"));

        if(chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String Path = chooser.getSelectedFile().getAbsolutePath();
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(Path));
                document.open();

                com.itextpdf.text.Font font = FontFactory.getFont(FontFactory.COURIER, 12);

                // Masukkan teks per baris agar spasi terjaga
                String[] lines = txtPreview.getText().split("/n");
                for(String line : lines) {
                    document.add(new Paragraph(line, font));
                }

                document.close();
                JOptionPane.showMessageDialog(this, "Nota Berhasil Disimpan ke PDF\nLokasi:" + Path);
                this.dispose();
            } catch(Exception e ) {
                JOptionPane.showMessageDialog(this, "Eror Saat Membuat PDF" + e.getMessage());
            }
        }
    }

    // UNTUK TEST TAMPILAN
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            // 1. Siapkan Customer
            model.Customer cust = new model.Customer(1, "Budi Santoso", "0812345", "Pontianak");

            // 2. Siapkan List untuk menampung barang-barang belanjaan
            java.util.List<model.DetailPenjualan> listBelanja = new java.util.ArrayList<>();

            // --- TAMBAH BARANG 1 ---
            model.Barang b1 = new model.Barang();
            b1.setIdBarang("B001");
            b1.setNamaBarang("Raket Yonex Nano");
            b1.setHargaJual(50000000);
            b1.setDiskon(10); // Diskon 10%

            model.DetailPenjualan d1 = new model.DetailPenjualan(1, 0, b1, 2); // Beli 2
            listBelanja.add(d1);

            // --- TAMBAH BARANG 2 ---
            model.Barang b2 = new model.Barang();
            b2.setIdBarang("B002");
            b2.setNamaBarang("Bola Kasti");
            b2.setHargaJual(2500000);
            b2.setDiskon(0); // Gak diskon

            model.DetailPenjualan d2 = new model.DetailPenjualan(2, 0, b2, 5); // Beli 5
            listBelanja.add(d2);

            // 3. Siapkan Header Penjualan
            model.Penjualan p = new model.Penjualan();
            p.setNoNota("NOTA-2023-001");
            p.setTanggal(new java.sql.Date(System.currentTimeMillis()));
            p.setCustomer(cust);
            p.setNamaKasir("Admin Amatir");
            p.setListDetail(listBelanja); // Masukkan list belanja tadi
            
            // PENTING: Hitung total bayarnya
            p.hitungTotalBayar();

            // 4. Tampilkan Dialog
            new CetakNota(null, true, p, listBelanja).setVisible(true);
        });
    }
}
