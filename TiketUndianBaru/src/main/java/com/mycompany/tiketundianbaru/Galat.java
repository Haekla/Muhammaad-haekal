package com.mycompany.tiketundianbaru;

public final class Galat {

    private Galat() {
    }

    public static String ramah(Throwable galat) {
        String pesan = SpringKonteks.pesanGalat(galat);
        String kecil = pesan.toLowerCase();
        if (kecil.contains("foreign key")) {
            return "Data ini masih dipakai data lain, jadi tidak bisa dihapus.";
        }
        if (kecil.contains("doesn't exist") || kecil.contains("does not exist")) {
            return "Tabel database belum ada. Nyalakan MySQL lalu jalankan ulang aplikasi."
                    + " Detail: " + pesan;
        }
        if (kecil.contains("communications link failure") || kecil.contains("connection refused")) {
            return "Tidak bisa terhubung ke MySQL. Pastikan MySQL di XAMPP sudah menyala.";
        }
        return pesan;
    }
}