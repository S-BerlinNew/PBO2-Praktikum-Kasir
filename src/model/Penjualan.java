package model;

import java.sql.Date;

public class Penjualan {
    private String noNota;
    private Date tanggal;
    private Customer customer;
    private String namaKasir;
    private String metodePembayaran;
    private double diskon;
    private double totalBayar;
    private DetailPenjualan detailPenjualan;

    public Penjualan() { }
    // konstruktor
    public Penjualan(String noNota, Date tanggal, Customer customer, String namaKasir, 
    String metodePembayaran, DetailPenjualan detailPenjualan) 
    {
        this.noNota = noNota;
        this.tanggal = tanggal;
        this.customer = customer;
        this.namaKasir = namaKasir;
        this.metodePembayaran = metodePembayaran;
        this.detailPenjualan = detailPenjualan;
    }

    // getter
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

    public double getDiskon() {
        return diskon;
    }

    public double getTotalBayar() {
        return totalBayar;
    }

    // Setter
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

    public void setDiskon(double diskon) {
        this.diskon = diskon;
    }

    public void setTotalBayar(double totalBayar) {
        this.totalBayar = totalBayar;
    }

    // Method
    public void hitungDiskon() {
        this.diskon = (diskon / 100) * detailPenjualan.getSubtotal();
    }

    public void hitungTotalBayar() {
        this.totalBayar = detailPenjualan.getSubtotal() - diskon;
    }
}
