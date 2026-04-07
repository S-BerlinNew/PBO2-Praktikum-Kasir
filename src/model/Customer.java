package model;

public class Customer {
    private int idCustomer;
    private String kodeCustomer;
    private String namaCustomer;
    private String noTelp;
    
    public Customer() { }
    // konstruktor
    public Customer(int idCustomer, String kodeCustomer, String namaCustomer, String noTelp) {
        this.idCustomer = idCustomer;
        this.kodeCustomer = kodeCustomer;
        this.namaCustomer = namaCustomer;
        this.noTelp = noTelp;
    }

    // getter
    public int getIdCustomer() {
        return idCustomer;
    }

    public String getKodeCustomer() {
        return kodeCustomer;
    }

    public String getNamaCustomer() {
        return namaCustomer;
    }

    public String getNoTelp() {
        return noTelp;
    }

    // Setter
    public void setIdCustomer(int idCustomer) {
        this.idCustomer = idCustomer;
    }

    public void setKodeCustomer(String kodeCustomer) {
        this.kodeCustomer = kodeCustomer;
    }

    public void setNamaCustomer(String namaCustomer) {
        this.namaCustomer = namaCustomer;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }
}
