package ui;

import dao.AkunDAO;
import java.awt.*;
import javax.swing.*;
import model.Akun;
import model.UserSession;

public class LoginForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox showPassword;

    public LoginForm() {
        setTitle("Login Sistem Penjualan");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(245, 247, 250));

        // ================= WRAPPER =================
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        // ================= CARD =================
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        card.setPreferredSize(new Dimension(300, 240));

        // ================= TITLE =================
        JLabel lblTitle = new JLabel("LOGIN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ================= INPUT =================
        txtUsername = new JTextField();
        styleTextField(txtUsername, "Username");

        txtPassword = new JPasswordField();
        stylePasswordField(txtPassword, "Password");

        // 🔥 SHOW PASSWORD
        showPassword = new JCheckBox("Tampilkan Password");
        showPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        showPassword.setBackground(Color.WHITE);

        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0); // tampilkan
            } else {
                txtPassword.setEchoChar('•'); // sembunyikan
            }
        });

        // ================= BUTTON =================
        ModernButton btnLogin = new ModernButton("LOGIN",
                new Color(52, 152, 219),
                new Color(41, 128, 185));

        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(200, 40));

        btnLogin.addActionListener(e -> prosesLogin());
        getRootPane().setDefaultButton(btnLogin);

        // ================= SUSUN =================
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(20));
        card.add(txtUsername);
        card.add(Box.createVerticalStrut(10));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(5));
        card.add(showPassword);
        card.add(Box.createVerticalStrut(15));
        card.add(btnLogin);

        wrapper.add(card);
        add(wrapper, BorderLayout.CENTER);
    }

    // ================= STYLE USERNAME =================
    private void styleTextField(JTextField field, String hint) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBackground(Color.WHITE);
        field.setForeground(Color.GRAY);
        field.setText(hint);

        field.setAlignmentX(Component.CENTER_ALIGNMENT);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(hint)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(hint);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    // ================= STYLE PASSWORD =================
    private void stylePasswordField(JPasswordField field, String hint) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBackground(Color.WHITE);
        field.setForeground(Color.GRAY);
        field.setText(hint);
        field.setEchoChar((char) 0); // biar hint kelihatan

        field.setAlignmentX(Component.CENTER_ALIGNMENT);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(hint)) {
                    field.setText("");
                    field.setEchoChar('•');
                    field.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setText(hint);
                    field.setEchoChar((char) 0);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    // ================= LOGIN PROCESS =================
    private void prosesLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        AkunDAO aDAO = new AkunDAO();
        Akun akun = aDAO.cekLogin(username, password);

        if (akun != null) {
            JOptionPane.showMessageDialog(this,
                    "Login Berhasil!\nSelamat Datang, " + akun.getNamaLengkap());

            UserSession.setRole(akun.getRole());

            new MainMenu(akun).setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Username atau Password salah!",
                    "Login Gagal",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= CUSTOM BUTTON =================
    class ModernButton extends JButton {
        private Color baseColor;
        private Color hoverColor;
        private boolean isHover = false;

        public ModernButton(String text, Color base, Color hover) {
            super(text);
            this.baseColor = base;
            this.hoverColor = hover;

            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    isHover = true;
                    repaint();
                }

                public void mouseExited(java.awt.event.MouseEvent e) {
                    isHover = false;
                    repaint();
                }
            });
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color warna = isHover ? hoverColor : baseColor;

            GradientPaint gp = new GradientPaint(
                    0, 0, warna.brighter(),
                    0, getHeight(), warna.darker()
            );

            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}