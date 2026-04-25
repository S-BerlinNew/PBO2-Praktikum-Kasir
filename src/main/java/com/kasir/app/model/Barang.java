package com.kasir.app.model;

public class Barang {
    private String idBarang;
    private String namaBarang;
    private double hargaJual;
    private double hargaModal;
    private String jenisBarang;
    private String brand;
    private String warna;
    private int stok;
    private int status; 
    
    public Barang() { }

    public Barang (String idBarang, String namaBarang, double hargaJual, double hargaModal, String jenisBarang,
        String brand, String warna, int stok, int status)
         {
            this.idBarang = idBarang;
            this.namaBarang = namaBarang;
            this.hargaJual = hargaJual;
            this.hargaModal = hargaModal;
            this.jenisBarang = jenisBarang;
            this.brand = brand;
            this.warna = warna;
            this.stok = stok;
            this.status = status;
    }

    
    public String getIdBarang() {
        return idBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public double getHargaJual() {
        return hargaJual;
    }

    public double getHargaModal() {
        return hargaModal;
    }

    public String getJenisBarang() {
        return jenisBarang;
    }

    public String getBrand() {
        return brand;
    }

    public String getWarna() {
        return warna;
    }

    public int getStok() {
        return stok;
    }

    public int getStatus() {
        return status;
    }


    public void setIdBarang(String idBarang) {
        this.idBarang = idBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public void setHargaJual(double hargaJual) {
        this.hargaJual = hargaJual;
    }

    public void setHargaModal(double hargaModal) {
        this.hargaModal = hargaModal;
    }

    public void setJenisBarang(String jenisBarang) {
        this.jenisBarang = jenisBarang;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setWarna(String warna) {
        this.warna = warna;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
}
