package model;

public class DetailPenjualan {
    private int idDetail;
    private int idPenjualan;
    private Barang barang;
    private int qty;
    private double subtotal;

    public DetailPenjualan() { }
    // konstruktor
    public DetailPenjualan(int idDetail, int idPenjualan, Barang barang, int qty) {
        this.idDetail = idDetail;
        this.idPenjualan = idPenjualan;
        this.barang = barang;
        this.qty = qty;
        updateSubtotal();
    }

    private void updateSubtotal() {
        double hargaKotor = barang.getHargaJual() * qty;
        double nilaiDiskon = hargaKotor * (barang.getDiskon() / 100);
        this.subtotal = hargaKotor - nilaiDiskon;
    }

    // getter
    public int getIdDetail() {
        return idDetail;
    }

    public int getIdPenjualan() {
        return idPenjualan;
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

    public void setIdPenjualan(int idPenjualan) {
        this.idPenjualan = idPenjualan;
    }

    public void setBarang(Barang barang) {
        this.barang = barang;
        updateSubtotal();
    }

    public void setQty(int qty) {
        this.qty = qty;
        updateSubtotal();
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

}
