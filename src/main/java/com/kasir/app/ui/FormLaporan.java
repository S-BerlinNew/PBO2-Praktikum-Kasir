package com.kasir.app.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.kasir.app.config.KoneksiDatabase;

public class FormLaporan extends JPanel {

    private JTable tabelMaster, tabelLog;
    private DefaultTableModel modelMaster, modelLog;
    private TableRowSorter<DefaultTableModel> sorterMaster, sorterLog;

    private JTextField txtCari, txtCariKasir, txtCariLog;
    private JComboBox<String> cbBayar;
    private JSpinner spinTglAwal, spinTglAkhir;

    private JPanel panelFilterMaster, panelFilterLog, panelUtamaFilter;

    public FormLaporan() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ==== MODEL ====
        modelMaster = new DefaultTableModel(new String[]{
            "No Nota", "Tanggal", "Kasir", "ID Barang", "Nama Barang",
            "Qty", "Harga Jual", "Diskon (%)", "Subtotal", "M-Pembayaran", "Harga Modal", "Keuntungan"
        }, 0);

        modelLog = new DefaultTableModel(new String[]{"Waktu", "User", "Aktivitas"}, 0);

        sorterMaster = new TableRowSorter<>(modelMaster);
        sorterLog = new TableRowSorter<>(modelLog);

        // ==== FILTER ====
        panelUtamaFilter = new JPanel(new CardLayout());
        panelUtamaFilter.setBorder(BorderFactory.createTitledBorder("Filter Laporan"));

        // FILTER TRANSAKSI
        panelFilterMaster = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        txtCari = new JTextField(10);
        txtCariKasir = new JTextField(10);

        cbBayar = new JComboBox<>(new String[]{"Semua", "Cash", "QRIS", "Transfer"});

        spinTglAwal = new JSpinner(new SpinnerDateModel());
        spinTglAwal.setEditor(new JSpinner.DateEditor(spinTglAwal, "yyyy-MM-dd"));

        spinTglAkhir = new JSpinner(new SpinnerDateModel());
        spinTglAkhir.setEditor(new JSpinner.DateEditor(spinTglAkhir, "yyyy-MM-dd"));

        JButton btnFilterTgl = new JButton("Filter");
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

        // FILTER LOG
        panelFilterLog = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        txtCariLog = new JTextField(20);
        JButton btnResetLog = new JButton("Reset");

        panelFilterLog.add(new JLabel("Cari:"));
        panelFilterLog.add(txtCariLog);
        panelFilterLog.add(btnResetLog);

        panelUtamaFilter.add(panelFilterMaster, "TRANSAKSI");
        panelUtamaFilter.add(panelFilterLog, "LOG");

        add(panelUtamaFilter, BorderLayout.NORTH);

        // ==== TAB ====
        JTabbedPane tabbedPane = new JTabbedPane();

        tabelMaster = new JTable(modelMaster);
        tabelMaster.setRowSorter(sorterMaster);
        tabbedPane.addTab("Transaksi", new JScrollPane(tabelMaster));

        tabelLog = new JTable(modelLog);
        tabelLog.setRowSorter(sorterLog);
        tabbedPane.addTab("Log", new JScrollPane(tabelLog));

        add(tabbedPane, BorderLayout.CENTER);

        // ==== EVENT ====
        tabbedPane.addChangeListener(e -> {
            CardLayout cl = (CardLayout) panelUtamaFilter.getLayout();
            if (tabbedPane.getSelectedIndex() == 0) {
                cl.show(panelUtamaFilter, "TRANSAKSI");
                loadDataMaster();
            } else {
                cl.show(panelUtamaFilter, "LOG");
                loadDataLog();
            }
        });

        txtCari.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { filter(); }
        });

        txtCariKasir.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { filter(); }
        });

        cbBayar.addActionListener(e -> filter());

        txtCariLog.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                sorterLog.setRowFilter(RowFilter.regexFilter("(?i)" + txtCariLog.getText()));
            }
        });

        btnFilterTgl.addActionListener(e -> {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            loadDataMasterDenganFilter(
                sdf.format(spinTglAwal.getValue()),
                sdf.format(spinTglAkhir.getValue())
            );
        });

        btnReset.addActionListener(e -> {
            txtCari.setText("");
            txtCariKasir.setText("");
            cbBayar.setSelectedIndex(0);
            sorterMaster.setRowFilter(null);
            loadDataMaster();
        });

        btnResetLog.addActionListener(e -> {
            txtCariLog.setText("");
            sorterLog.setRowFilter(null);
            loadDataLog();
        });

        loadDataMaster();
        loadDataLog();
    }

    private void filter() {
        java.util.List<RowFilter<Object, Object>> filters = new java.util.ArrayList<>();

        if (!txtCari.getText().isEmpty())
            filters.add(RowFilter.regexFilter("(?i)" + txtCari.getText(), 0));

        if (!txtCariKasir.getText().isEmpty())
            filters.add(RowFilter.regexFilter("(?i)" + txtCariKasir.getText(), 2));

        if (!cbBayar.getSelectedItem().toString().equals("Semua"))
            filters.add(RowFilter.regexFilter(cbBayar.getSelectedItem().toString(), 9));

        sorterMaster.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
    }

    private void loadDataMaster() {
        String sql = "SELECT p.no_nota, p.tanggal, p.nama_kasir, d.id_barang, b.nama_barang, " +
                     "d.qty, b.harga_jual, p.diskon, d.subtotal, p.metode_pembayaran, b.harga_modal, " +
                     "(d.subtotal - (b.harga_modal * d.qty)) AS untung " +
                     "FROM detail_penjualan d " +
                     "JOIN penjualan p ON d.id_penjualan = p.id_penjualan " +
                     "JOIN barang b ON d.id_barang = b.id_barang " +
                     "ORDER BY p.tanggal DESC";

        eksekusi(sql);
    }

    private void loadDataMasterDenganFilter(String a, String b) {
        String sql = "SELECT p.no_nota, p.tanggal, p.nama_kasir, d.id_barang, b.nama_barang, " +
                     "d.qty, b.harga_jual, p.diskon, d.subtotal, p.metode_pembayaran, b.harga_modal, " +
                     "(d.subtotal - (b.harga_modal * d.qty)) AS untung " +
                     "FROM detail_penjualan d " +
                     "JOIN penjualan p ON d.id_penjualan = p.id_penjualan " +
                     "JOIN barang b ON d.id_barang = b.id_barang " +
                     "WHERE p.tanggal BETWEEN '" + a + "' AND '" + b + "'";

        eksekusi(sql);
    }

    private void eksekusi(String sql) {
        modelMaster.setRowCount(0);

        try (Connection conn = KoneksiDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                modelMaster.addRow(new Object[]{
                    rs.getString(1), rs.getDate(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getInt(6),
                    rs.getDouble(7), rs.getInt(8), rs.getDouble(9),
                    rs.getString(10), rs.getDouble(11), rs.getDouble(12)
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void loadDataLog() {
        modelLog.setRowCount(0);

        try (Connection conn = KoneksiDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT l.waktu, a.username, l.aksi FROM log_aktivitas l JOIN akun a ON l.id_akun = a.id_akun ORDER BY l.waktu DESC");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                modelLog.addRow(new Object[]{
                    rs.getTimestamp(1),
                    rs.getString(2),
                    rs.getString(3)
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}