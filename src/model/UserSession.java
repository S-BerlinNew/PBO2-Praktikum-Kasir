package model;

public class UserSession {
    private static String role;

    public static void setRole(String r) {
        role = r;
    }

    public static String getRole() {
        return role;
    }
}
