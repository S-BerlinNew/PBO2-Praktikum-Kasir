package ui;

import java.awt.*;
import javax.swing.*;
import dao.AkunDAO;
import model.Akun;
import model.UserSession;


public class LoginForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginForm() {
        setTitle("Login Sistem Penjualan");
        setSize(350, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));

        // Judul
        JLabel lblTitle = new JLabel("LOGIN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle, BorderLayout.NORTH);

        // Panel input
        JPanel panelInput = new JPanel(new GridLayout(2,2,5,5));
        panelInput.setBorder(BorderFactory.createEmptyBorder(10,30,10,20));

        panelInput.add(new JLabel("Username"));
        txtUsername = new JTextField();
        panelInput.add(txtUsername);

        panelInput.add(new JLabel("Password"));
        txtPassword = new JPasswordField();
        panelInput.add(txtPassword);

        add(panelInput, BorderLayout.CENTER);

        // Tombol login
        JButton btnLogin = new JButton("LOGIN");

        btnLogin.addActionListener(e -> prosesLogin());

        JPanel panelButton = new JPanel();
        panelButton.add(btnLogin);
        getRootPane().setDefaultButton(btnLogin);

        add(panelButton, BorderLayout.SOUTH);
    }

    private void prosesLogin() {
    String username = txtUsername.getText();
    String password = new String(txtPassword.getPassword());

        // 1. Panggil AkunDAO untuk cek ke Database
        AkunDAO aDAO = new AkunDAO();
        Akun akun = aDAO.cekLogin(username, password);

        // 2. Validasi hasilnya
        if (akun != null) {
            JOptionPane.showMessageDialog(this, "Login Berhasil!\nSelamat Datang, " + akun.getNamaLengkap());
            UserSession.setRole(akun.getRole());
            // 3. Buka MainMenu sambil MENGIRIM data akun
            new MainMenu(akun).setVisible(true); 

            this.dispose(); // Tutup form login
        } else {
            JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new LoginForm().setVisible(true);

        });

    }
}
