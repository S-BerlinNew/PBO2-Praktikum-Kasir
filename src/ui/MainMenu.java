package ui;

import java.awt.*;
import javax.swing.*;
import model.Akun;

public class MainMenu extends JFrame {
    private Akun userLogin;
    private JButton activeButton = null;
    private JPanel content; // 🔥 panel utama untuk ganti isi

    public MainMenu(Akun akun) {
        this.userLogin = akun;

        setTitle("Sistem Penjualan Alat Olahraga - v1.0");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ================= SIDEBAR =================
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(230, getHeight()));
        sidebar.setBackground(new Color(36, 52, 71));

        // ================= HEADER =================
        JPanel header = new JPanel();
        header.setBackground(new Color(30, 44, 60));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(25, 10, 25, 10));

        JLabel title = new JLabel("TOKO OLAHRAGA");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel user = new JLabel(userLogin.getNamaLengkap());
        user.setForeground(new Color(180, 180, 180));
        user.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        user.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(5));
        header.add(user);

        // ================= MENU =================
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(new Color(36, 52, 71));
        menu.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JButton btnBarang = createMenuButton("Data Barang");
        JButton btnCustomer = createMenuButton("Data Customer");
        JButton btnPenjualan = createMenuButton("Transaksi");
        JButton btnAkun = createMenuButton("Akun Admin");

        menu.add(btnBarang);
        menu.add(Box.createVerticalStrut(10));
        menu.add(btnCustomer);
        menu.add(Box.createVerticalStrut(10));
        menu.add(btnPenjualan);
        menu.add(Box.createVerticalStrut(10));
        menu.add(btnAkun);

        // ================= LOGOUT =================
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(new Color(36, 52, 71));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(231, 76, 60));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogout.setBackground(new Color(192, 57, 43));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogout.setBackground(new Color(231, 76, 60));
            }
        });

        bottom.add(btnLogout, BorderLayout.CENTER);

        // ================= SIDEBAR GABUNG =================
        sidebar.add(header, BorderLayout.NORTH);
        sidebar.add(menu, BorderLayout.CENTER);
        sidebar.add(bottom, BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);

        // ================= CONTENT =================
        content = new JPanel(new BorderLayout());
        content.setBackground(new Color(245, 247, 250));
        add(content, BorderLayout.CENTER);

        // 🔥 default halaman awal
        setContent(createWelcomePanel());

        // ================= NAVIGASI =================
        btnBarang.addActionListener(e -> {
            setActiveButton(btnBarang);
            setContent(new FormBarang()); // 🔥 HARUS JPanel
        });

        btnCustomer.addActionListener(e -> {
            setActiveButton(btnCustomer);
            setContent(new FormCustomer());
        });

        btnPenjualan.addActionListener(e -> {
            setActiveButton(btnPenjualan);
            setContent(new FormPenjualan(userLogin));
        });

        btnAkun.addActionListener(e -> {
            setActiveButton(btnAkun);
            setContent(new FormKelolaAkunPanel()); // 🔥 ubah jadi JPanel
        });

        btnLogout.addActionListener(e -> {
            int konfirm = JOptionPane.showConfirmDialog(this, "Yakin logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (konfirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginForm().setVisible(true);
            }
        });

        // role kasir
        if (userLogin.getRole().equalsIgnoreCase("kasir")) {
            btnBarang.setEnabled(false);
            btnAkun.setVisible(false);
        }
    }

    // ================= GANTI CONTENT =================
    private void setContent(JPanel panel) {
        content.removeAll();
        content.add(panel, BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
    }

    // ================= DEFAULT PANEL =================
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));

        JLabel label = new JLabel("Selamat Datang di Sistem Kasir", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(new Color(44, 62, 80));

        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    // ================= BUTTON =================
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);

        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setBackground(new Color(36, 52, 71));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn != activeButton) {
                    btn.setBackground(new Color(52, 73, 94));
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn != activeButton) {
                    btn.setBackground(new Color(36, 52, 71));
                }
            }
        });

        return btn;
    }

    // ================= ACTIVE BUTTON =================
    private void setActiveButton(JButton btn) {
        if (activeButton != null) {
            activeButton.setBackground(new Color(36, 52, 71));
        }

        btn.setBackground(new Color(41, 128, 185));
        activeButton = btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Akun dummy = new Akun(1, "admin", "123", "BOSS Berlin", "admin");
            new MainMenu(dummy).setVisible(true);
        });
    }
}