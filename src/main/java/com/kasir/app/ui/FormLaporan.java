package com.kasir.app.ui;

import javax.swing.*;
import javax.swing.table.*;
import com.kasir.app.config.KoneksiDatabase;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FormLaporan extends JFrame {
    private JTable tabelMaster, tabelLog;
    private DefaultTableModel modelMaster, modelLog;
    private TableRowSorter<DefaultTableModel> sorterMaster, sorterLog;
    private JTextField txtCari, txtCariKasir, txtCariLog;
    private JComboBox<String> cbBayar;
    private JSpinner spinTglAwal, spinTglAkhir;
    private JPanel panelFilterMaster, panelFilterLog, panelUtamaFilter;

    public FormLaporan() {
        setTitle("Laporan Penjualan & Aktivitas");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());


        
        // ==== MODEL & SORTER ====
        modelMaster = new DefaultTableModel(new String[]{
            "No Nota", "Tanggal", "Kasir", "ID Barang", "Nama Barang", 
            "Qty", "Harga Jual", "Diskon (%)", "Subtotal", "M-Pembayaran", "Harga Modal", "Keuntungan"
        }, 0);
        modelLog = new DefaultTableModel(new String[]{"Waktu", "User", "Aktivitas"}, 0);
        
        sorterMaster = new TableRowSorter<>(modelMaster);
        sorterLog = new TableRowSorter<>(modelLog);



        // ==== PANEL FILTER (DYNAMIC) ====
        panelUtamaFilter = new JPanel(new CardLayout());
        panelUtamaFilter.setBorder(BorderFactory.createTitledBorder("Filter Laporan"));

        // -- Filter untuk Transaksi --
        panelFilterMaster = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        txtCari = new JTextField(10);
        txtCariKasir = new JTextField(10);
        String[] bayar = {"Semua", "Cash", "QRIS", "Transfer"};
        cbBayar = new JComboBox<>(bayar);
        spinTglAwal = new JSpinner(new SpinnerDateModel());
        spinTglAwal.setEditor(new JSpinner.DateEditor(spinTglAwal, "yyyy-MM-dd"));
        spinTglAkhir = new JSpinner(new SpinnerDateModel());
        spinTglAkhir.setEditor(new JSpinner.DateEditor(spinTglAkhir, "yyyy-MM-dd"));
        JButton btnFilterTgl = new JButton("Filter Tanggal");
        JButton btnReset = new JButton("Reset");

        panelFilterMaster.add(new JLabel("No Nota:"));
        panelFilterMaster.add(txtCari);
        panelFilterMaster.add(new JLabel("Kasir:"));
        panelFilterMaster.add(txtCariKasir);
        panelFilterMaster.add(new JLabel("Metode:"));
        panelFilterMaster.add(cbBayar);
        panelFilterMaster.add(new JLabel("Tanggal:"));
        panelFilterMaster.add(spinTglAwal);
        panelFilterMaster.add(new JLabel("-"));
        panelFilterMaster.add(spinTglAkhir);
        panelFilterMaster.add(btnFilterTgl);
        panelFilterMaster.add(btnReset);

        // -- Filter untuk Log --
        panelFilterLog = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        txtCariLog = new JTextField(20);
        JButton btnResetLog = new JButton("Reset Log");
        panelFilterLog.add(new JLabel("Cari User / Aktivitas:"));
        panelFilterLog.add(txtCariLog);
        panelFilterLog.add(btnResetLog);

        panelUtamaFilter.add(panelFilterMaster, "TRANSAKSI");
        panelUtamaFilter.add(panelFilterLog, "LOG");
        add(panelUtamaFilter, BorderLayout.NORTH);



        //-- Tombol Back --
        JButton btnBack = new JButton("KEMBALI");
        btnBack.setBackground(Color.DARK_GRAY);
        btnBack.setForeground(Color.WHITE);
        btnBack.addActionListener(e -> this.dispose());

        JPanel panelBack = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBack.add(btnBack);

        add(panelBack, BorderLayout.SOUTH);




        // ==== TABBED PANE ====
        JTabbedPane tabbedPane = new JTabbedPane();
        tabelMaster = new JTable(modelMaster);
        tabelMaster.setRowSorter(sorterMaster);
        tabbedPane.addTab("Riwayat Transaksi", new JScrollPane(tabelMaster));

        tabelLog = new JTable(modelLog);
        tabelLog.setRowSorter(sorterLog);
        tabbedPane.addTab("Log Aktivitas", new JScrollPane(tabelLog));
        add(tabbedPane, BorderLayout.CENTER);




        // ==== LOGIKA PERUBAHAN TAB ====
        tabbedPane.addChangeListener(e -> {
            CardLayout cl = (CardLayout) (panelUtamaFilter.getLayout());
            if (tabbedPane.getSelectedIndex() == 0) {
                cl.show(panelUtamaFilter, "TRANSAKSI");
                loadDataMaster(); 
            } else {
                cl.show(panelUtamaFilter, "LOG");
                loadDataLog();   
            }
        });



        // ==== EVENT LISTENERS ====
        txtCari.addKeyListener(new KeyAdapter() { public void keyReleased(KeyEvent e) { jalankanFilterMaster(); } });
        txtCariKasir.addKeyListener(new KeyAdapter() { public void keyReleased(KeyEvent e) { jalankanFilterMaster(); } });
        cbBayar.addActionListener(e -> jalankanFilterMaster());
        
        txtCariLog.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                sorterLog.setRowFilter(RowFilter.regexFilter("(?i)" + txtCariLog.getText()));
            }
        });

        btnFilterTgl.addActionListener(e -> {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            loadDataMasterDenganFilter(sdf.format(spinTglAwal.getValue()), sdf.format(spinTglAkhir.getValue()));
        });

        btnReset.addActionListener(e -> {
            txtCari.setText(""); txtCariKasir.setText("");
            spinTglAwal.setValue(new java.util.Date()); spinTglAkhir.setValue(new java.util.Date());
            cbBayar.setSelectedIndex(0); sorterMaster.setRowFilter(null);
            loadDataMaster();
        });

        btnResetLog.addActionListener(e -> {
            txtCariLog.setText(""); sorterLog.setRowFilter(null);
            loadDataLog();
        });

        loadDataMaster();
        loadDataLog();
        setLocationRelativeTo(null);
    }

    private void jalankanFilterMaster() {
        java.util.List<RowFilter<Object, Object>> filters = new java.util.ArrayList<>();
        if (!txtCari.getText().isEmpty()) filters.add(RowFilter.regexFilter("(?i)" + txtCari.getText(), 0));
        if (!txtCariKasir.getText().isEmpty()) filters.add(RowFilter.regexFilter("(?i)" + txtCariKasir.getText(), 2));
        String m = cbBayar.getSelectedItem().toString();
        if (!m.equals("Semua")) filters.add(RowFilter.regexFilter(m, 9));
        sorterMaster.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
    }

    private void loadDataMaster() {
        String sql = "SELECT p.no_nota, p.tanggal, p.nama_kasir, d.id_barang, b.nama_barang, " +
                     "d.qty, b.harga_jual, p.diskon, d.subtotal, p.metode_pembayaran, b.harga_modal, " +
                     "(d.subtotal - (b.harga_modal * d.qty)) AS untung " +
                     "FROM detail_penjualan d " +
                     "JOIN penjualan p ON d.id_penjualan = p.id_penjualan " +
                     "JOIN barang b ON d.id_barang = b.id_barang " +
                     "ORDER BY p.tanggal DESC, p.no_nota DESC";
        eksekusiQuery(sql);
    }

    private void loadDataMasterDenganFilter(String tglAwal, String tglAkhir) {
        String sql = "SELECT p.no_nota, p.tanggal, p.nama_kasir, d.id_barang, b.nama_barang, " +
                     "d.qty, b.harga_jual, p.diskon, d.subtotal, p.metode_pembayaran, b.harga_modal, " +
                     "(d.subtotal - (b.harga_modal * d.qty)) AS untung " +
                     "FROM detail_penjualan d " +
                     "JOIN penjualan p ON d.id_penjualan = p.id_penjualan " +
                     "JOIN barang b ON d.id_barang = b.id_barang " +
                     "WHERE p.tanggal BETWEEN '" + tglAwal + "' AND '" + tglAkhir + "' " +
                     "ORDER BY p.tanggal DESC, p.no_nota DESC";
        eksekusiQuery(sql);
    }

    private void eksekusiQuery(String sql) {
        modelMaster.setRowCount(0);
        try (Connection conn = KoneksiDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modelMaster.addRow(new Object[]{
                    rs.getString("no_nota"), rs.getDate("tanggal"), rs.getString("nama_kasir"),
                    rs.getString("id_barang"), rs.getString("nama_barang"), rs.getInt("qty"),
                    rs.getDouble("harga_jual"), rs.getInt("diskon"), rs.getDouble("subtotal"),
                    rs.getString("metode_pembayaran"), rs.getDouble("harga_modal"), rs.getDouble("untung")
                });
            }
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void loadDataLog() {
        modelLog.setRowCount(0);
        String sql = "SELECT l.waktu, a.username, l.aksi " +
                 "FROM log_aktivitas l " +
                 "JOIN akun a ON l.id_akun = a.id_akun " + 
                 "ORDER BY l.waktu DESC";
        try (Connection conn = KoneksiDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modelLog.addRow(new Object[]{rs.getTimestamp("waktu"), rs.getString("username"), rs.getString("aksi")});
            }
        } catch (SQLException e) { System.out.println("Log belum siap."); 
            e.printStackTrace();
        }
    }
}