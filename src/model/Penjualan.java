package model;

import java.sql.Date;
import java.util.List;

public class Penjualan {
    private int idPenjualan;
    private String noNota;
    private Date tanggal;
    private Customer customer;
    private String namaKasir;
    private String metodePembayaran;
    
    private double totalBayar;
    private List<DetailPenjualan> listDetail;

    public Penjualan() { }
    // konstruktor
    public Penjualan(int idPenjualan, String noNota, Date tanggal, Customer customer, String namaKasir, 
    String metodePembayaran, List<DetailPenjualan> listDetail) 
    {
        this.idPenjualan = idPenjualan;
        this.noNota = noNota;
        this.tanggal = tanggal;
        this.customer = customer;
        this.namaKasir = namaKasir;
        this.metodePembayaran = metodePembayaran;
        this.listDetail = listDetail;
    }

    // getter
    public int getIdPenjualan() {
        return idPenjualan;
    }

    public String getNoNota() {
        return noNota;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getNamaKasir() {
        return namaKasir;
    }

    public String getMetodePembayaran() {
        return metodePembayaran;
    }

    public double getTotalBayar() {
        return totalBayar;
    }

    public List<DetailPenjualan> getListDetail() {
        return listDetail;
    }

    // Setter
    public void setIdPenjualan(int idPenjualan) {
        this.idPenjualan = idPenjualan;
    }

    public void setNoNota(String noNota) {
        this.noNota = noNota;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setNamaKasir(String namaKasir) {
        this.namaKasir = namaKasir;
    }

    public void setMetodePembayaran(String metodePembayaran) {
        this.metodePembayaran = metodePembayaran;
    }

    public void setTotalBayar(double totalBayar) {
        this.totalBayar = totalBayar;
    }

    public void setListDetail(List<DetailPenjualan> listDetail) {
        this.listDetail = listDetail;
    }

    // Method
    public void hitungTotalBayar() {
        double total = 0;
        if(listDetail != null) {
            for (DetailPenjualan dp : listDetail) {
                total += dp.getSubtotal();
            }
        }
        this.totalBayar = total;
    }
}
