package ui;

import javax.swing.*;

import java.awt.*;

public class MainMenu extends JFrame {

    public MainMenu() {
        setTitle("Sistem Penjualan Alat Olahraga - v1.0");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(20, 20));

        // Header
        JLabel labelJudul = new JLabel("MENU UTAMA TOKO OLAHRAGA", SwingConstants.CENTER);
        labelJudul.setFont(new Font("Arial", Font.BOLD, 22));
        labelJudul.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        add(labelJudul, BorderLayout.NORTH);

        // Panel Tengah Buat Tombol"
        JPanel panelMenu = new JPanel(new GridLayout(3, 1, 15, 15));
        panelMenu.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton btnBarang = new JButton("KELOLA DATA BARANG");
        JButton btnCustomer = new JButton("KELOLA DATA CUSTOMER");
        JButton btnPenjualan = new JButton("TRANSAKSI PENJUALAN");

        // Logika Navigasi
        btnBarang.addActionListener(e -> {
            new FormBarang().setVisible(true);
        });

        btnPenjualan.addActionListener(e -> {
            new FormPenjualan().setVisible(true);
        });

        btnCustomer.addActionListener(e -> {
            new FormCustomer().setVisible(true);
        });

        panelMenu.add(btnBarang);
        panelMenu.add(btnCustomer);
        panelMenu.add(btnPenjualan);

        add(panelMenu, BorderLayout.CENTER);

        // Footer
        JLabel labelFooter = new JLabel("Developer Baru Berkembang ITBSS © 2026", SwingConstants.CENTER);
        add(labelFooter, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new MainMenu().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
}
