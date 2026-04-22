package model;

public class Akun {
    private int id_akun;
    private String username;
    private String password;
    private String nama_lengkap;
    private String role;
    
    public Akun() {}

    public Akun(int id_akun, String username, String password, String nama_lengkap, String role) {
        this.id_akun = id_akun;
        this.username = username;
        this.password = password;
        this.nama_lengkap = nama_lengkap;
        this.role = role;
    }

    public int getIdAkun() {
        return id_akun;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNamaLengkap() {
        return nama_lengkap;
    }

    public String getRole(){
        return role;
    }

    // setter
    public void setIdAkun(int id_akun) {
        this.id_akun = id_akun;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNamaLengkap(String nama_lengkap) {
        this.nama_lengkap = nama_lengkap;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
