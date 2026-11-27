package com.mycompany.tiketundianbaru;

public class UjiSpring {

    public static void main(String[] args) {
        try {
            System.out.println("1. Spring menyiapkan Hibernate dan koneksi MySQL...");
            AdminDao dao = SpringKonteks.ambil(AdminDao.class);
            System.out.println("   Berhasil. Username 'admin' ada di tabel: " + dao.usernameSudahAda("admin"));

            System.out.println("2. Uji login admin / admin123...");
            Admin admin = dao.login("admin", "admin123");
            System.out.println(admin != null
                    ? "   Berhasil. Ditemukan: " + admin
                    : "   Tidak cocok. Periksa isi tabel admin atau passwordnya.");

            System.out.println("3. Hash SHA-256 dari 'admin123' di Java: " + AdminDao.sha256("admin123"));
            System.out.println("   Nilai ini harus sama dengan kolom password milik admin di phpMyAdmin.");
        } catch (RuntimeException e) {
            System.out.println("GAGAL: " + SpringKonteks.pesanGalat(e));
            e.printStackTrace();
        } finally {
            SpringKonteks.tutup();
        }
    }
}
