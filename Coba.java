import javax.swing.*; // Ini cara manggil fitur Swing-nya

public class Coba {
    public static void main(String[] args) {
        // 1. Bikin jendelanya
        JFrame frame = new JFrame("Toko Badminton Gue");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 2. Bikin tombolnya
        JButton tombol = new JButton("Klik Saya, Bro!");

        // 3. Masukin tombol ke jendela
        frame.add(tombol);

        // 4. Munculin jendelanya
        frame.setVisible(true);
    }
}