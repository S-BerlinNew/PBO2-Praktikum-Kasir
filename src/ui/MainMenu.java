package ui;
import model.Akun;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Flow;



public class MainMenu extends JFrame {
    private Akun userLogin; //Variabel untuk simpan siapa yang login

    public MainMenu(Akun akun) {
        this.userLogin = akun; //Simpan data login
        
        setTitle("Sistem Penjualan Alat Olahraga - v1.0");
        setSize(550, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(20, 20));

        // Header
        JPanel panelHeader = new JPanel(new GridLayout(2, 1));
        JLabel labelJudul = new JLabel("MENU UTAMA TOKO OLAHRAGA", SwingConstants.CENTER);
        labelJudul.setFont(new Font("Arial", Font.BOLD, 22));

        // Label Sapaan saat berhasil login
        JLabel labelUser = new JLabel("Selamat Datang, " + userLogin.getNamaLengkap() + " (" + userLogin.getRole() + ")", SwingConstants.CENTER);
        labelUser.setForeground(Color.BLUE);

        panelHeader.add(labelJudul);
        panelHeader.add(labelUser);
        add(panelHeader, BorderLayout.NORTH);

        // Panel Tengah Gridlayout
        JPanel panelMenu = new JPanel(new GridLayout(4, 1, 15, 15));
        panelMenu.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton btnBarang = new JButton("KELOLA DATA BARANG");
        JButton btnCustomer = new JButton("KELOLA DATA CUSTOMER");
        JButton btnPenjualan = new JButton("TRANSAKSI PENJUALAN");
        JButton btnAkun = new JButton("KELOLA AKUN ADMIN");
        JButton btnLogout = new JButton("LOGOUT");

        // LOGIKA HAK AKSES ROLE
        if(userLogin.getRole().equalsIgnoreCase("kasir")) {
            btnBarang.setEnabled(false);
            btnAkun.setVisible(false);
            btnBarang.setToolTipText("Hanya Admin yang bisa mengelola barang");
        }

        // Logika Navigasi
        btnBarang.addActionListener(e -> {
            new FormBarang().setVisible(true);
        });

        btnPenjualan.addActionListener(e -> {
            new FormPenjualan(this.userLogin).setVisible(true);
        });

        btnCustomer.addActionListener(e -> {
            new FormCustomer().setVisible(true);
        });

        btnAkun.addActionListener(e -> {
            FormKelolaAkun fa = new FormKelolaAkun(this, true);
            fa.setVisible(true);
        });

        btnLogout.addActionListener(e -> {
            int konfirm = JOptionPane.showConfirmDialog(this, 
                "Apakah Anda yakin ingin keluar dari sistem?", 
                "Konfirmasi Logout", 
                JOptionPane.YES_NO_OPTION);
                
            if (konfirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginForm().setVisible(true); 
            }
        });

        panelMenu.add(btnBarang);
        panelMenu.add(btnCustomer);
        panelMenu.add(btnPenjualan);
        panelMenu.add(btnAkun);


        add(panelMenu, BorderLayout.CENTER);

        JPanel barisSatu = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel barisDua = new JPanel(new FlowLayout(FlowLayout.CENTER));

        btnLogout.setBackground(new Color(220, 53, 69)); // Warna merah
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Arial", Font.BOLD, 12));
        barisSatu.add(btnLogout);


        // Footer
        JLabel labelFooter = new JLabel("Developer Baru Berkembang ITBSS © 2026", SwingConstants.CENTER);
        add(labelFooter, BorderLayout.SOUTH);
        barisDua.add(labelFooter);

        JPanel panelBawahFinal = new JPanel(new GridLayout(2, 1));
        panelBawahFinal.add(barisSatu); // Baris atas
        panelBawahFinal.add(barisDua);  // Baris bawah

        // 4. Tempel ke Frame Utama (SOUTH)
        // Pakai panelBawahFinal ya bro, jangan panelBawah lagi
        add(panelBawahFinal, BorderLayout.SOUTH);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                // Bikin Akun bohongan buat ngetes tampilan
                Akun adminDummy = new Akun(1, "admin", "123", "BOSS Berlin, Sean, Jobi", "admin");
                
                // Masukkan adminDummy ke dalam parameter
                new MainMenu(adminDummy).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
}
