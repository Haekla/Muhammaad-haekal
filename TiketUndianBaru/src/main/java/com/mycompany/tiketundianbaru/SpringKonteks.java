package com.mycompany.tiketundianbaru;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Pintu masuk ke Spring dari form Swing. Form tidak dibuat oleh Spring,
 * jadi form meminta objek DAO lewat kelas ini.
 * Konteks Spring dibuat saat pertama kali diminta, sehingga galat koneksi
 * muncul di form yang memintanya dan bisa ditampilkan sebagai pesan.
 */
public final class SpringKonteks {

    private static AnnotationConfigApplicationContext konteks;

    private SpringKonteks() {
    }

    public static synchronized <T> T ambil(Class<T> kelas) {
        if (konteks == null) {
            konteks = new AnnotationConfigApplicationContext(AppConfig.class);
        }
        return konteks.getBean(kelas);
    }

    public static synchronized void tutup() {
        if (konteks != null) {
            konteks.close();
            konteks = null;
        }
    }

    /** Mengambil pesan galat paling dalam, misalnya "Unknown database 'xxx'", supaya mudah dibaca. */
    public static String pesanGalat(Throwable galat) {
        Throwable akar = galat;
        for (int i = 0; i < 20 && akar.getCause() != null && akar.getCause() != akar; i++) {
            akar = akar.getCause();
        }
        String pesan = akar.getMessage();
        return (pesan == null || pesan.isEmpty()) ? akar.getClass().getSimpleName() : pesan;
    }
}
