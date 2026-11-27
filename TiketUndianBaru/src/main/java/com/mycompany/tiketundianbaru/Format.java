package com.mycompany.tiketundianbaru;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Format {

    private static final DateTimeFormatter TANGGAL = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter WAKTU = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private Format() {
    }

    public static String rupiah(long nilai) {
        String angka = Long.toString(Math.abs(nilai));
        StringBuilder hasil = new StringBuilder();
        for (int i = 0; i < angka.length(); i++) {
            if (i > 0 && (angka.length() - i) % 3 == 0) {
                hasil.append('.');
            }
            hasil.append(angka.charAt(i));
        }
        return (nilai < 0 ? "-Rp " : "Rp ") + hasil;
    }

    public static String tanggal(LocalDate tanggal) {
        return tanggal == null ? "" : tanggal.format(TANGGAL);
    }

    public static String waktu(LocalDateTime waktu) {
        return waktu == null ? "" : waktu.format(WAKTU);
    }
}