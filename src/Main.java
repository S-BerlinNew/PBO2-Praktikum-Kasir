import dao.*;
import model.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Memanggil DAO Detail Penjualan
        DetailPenjualanDAO dDAO = new DetailPenjualanDAO();
        // Ambil semua data dari database
        List<DetailPenjualan> semuaBelanjaan = dDAO.getAll();

        System.out.println("===Simulasi Cetak Nota===");
        System.out.printf("%-7s | %-20s | %-3s | %-13s | %-13s\n", "Nota", "Barang", "Qty", "Harga", "Subtotal");
        System.out.println("-----------------------------------------------------------------------");

        for(DetailPenjualan dp : semuaBelanjaan) {
            // memanggil barang data asli hasil dari getById
            String noNota = dp.getnoNota();
            String namaBarang = dp.getBarang().getNamaBarang();
            int qty = dp.getQty();
            double harga = dp.getBarang().getHargaJual();
            double sub = dp.getSubtotal();

            // %-10s artinya String dengan lebar 10 karakter rata kiri
        // %,.0f artinya angka desimal dengan pemisah ribuan
            System.out.printf("%-7s | %-20s | %-3d | Rp%,10.0f | Rp%,10.0f\n", 
            noNota, namaBarang, qty, harga, sub);
        }
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("STATUS: DAO AMAN TERKENDALI!");
    }
}
