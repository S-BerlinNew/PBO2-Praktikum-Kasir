package com.kasir.app.model;

public class UserSession {
    private static int idAkun; 
    private static String role;
    private static String username; 

    public static void setIdAkun(int id) { 
        idAkun = id; }

    public static int getIdAkun() { 
        return idAkun; }

    public static void setRole(String r) { 
        role = r; }

    public static String getRole() { 
        return role; }

    public static void setUsername(String u) { 
        username = u; }
        
    public static String getUsername() { return 
        username; }
}