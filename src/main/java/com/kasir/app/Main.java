package com.kasir.app;

import com.kasir.app.ui.LoginForm;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}