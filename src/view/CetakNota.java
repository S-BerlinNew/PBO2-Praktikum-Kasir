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
        setSize(450, 600);
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
        sb.append(String.format("%-20s %-3s %-10s %-10s", "Barang", "Qty", "Harga", "Subtotal\n"));
        sb.append("-------------------------------------------------\n");

        for(DetailPenjualan d : listDetail) {
            String nama = d.getBarang().getNamaBarang();
            if(nama.length() > 18 ) nama = nama.substring(0, 15) + "...";

            sb.append(String.format("%-20s %-3d %-10s.0f %-10.0f\n",
                    nama, d.getQty(), d.getBarang().getHargaJual(), d.getSubtotal()
            ));
        }

        sb.append("-------------------------------------------------\n");
        sb.append(String.format("Diskon          : %10.0f%%\n", p.getDiskon()));
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
}
