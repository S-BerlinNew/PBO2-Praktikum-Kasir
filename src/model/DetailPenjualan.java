package model;

public class DetailPenjualan {
    private int idDetail;
    private String NoNota;
    private Barang barang;
    private int qty;
    private double subtotal;

    public DetailPenjualan() { }
    // konstruktor
    public DetailPenjualan(int idDetail, String NoNota, Barang barang, int qty) {
        this.idDetail = idDetail;
        this.NoNota = NoNota;
        this.barang = barang;
        this.qty = qty;
       
        hitungOtomatis(); 
    }

    private void hitungOtomatis() {
        double hargaKotor = barang.getHargaJual() * qty;
        double nilaiDiskon = hargaKotor * (barang.getDiskon() / 100);
        this.subtotal = hargaKotor - nilaiDiskon;
    }

    // getter
    public int getIdDetail() {
        return idDetail;
    }

    public String getnoNota() {
        return NoNota;
    }

    public Barang getBarang() {
        return barang;
    }

    public int getQty() {
        return qty;
    }

    public double getSubtotal() {
        return subtotal;
    }

    // Setter
    public void setIdDetail(int idDetail) {
        this.idDetail = idDetail;
    }

    public void setnoNota(String NoNota) {
        this.NoNota = NoNota;
    }

    public void setBarang(Barang barang) {
        this.barang = barang;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setSubtotal(double subTotal) {
        this.subtotal = subTotal;
    }
}
