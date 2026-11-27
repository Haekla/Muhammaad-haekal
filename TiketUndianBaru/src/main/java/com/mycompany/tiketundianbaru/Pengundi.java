package com.mycompany.tiketundianbaru;

import java.util.List;
import java.util.Random;

public final class Pengundi {

    private Pengundi() {
    }

    public static Tiket pilih(List<Tiket> daftar, Random acak) {
        if (daftar == null || daftar.isEmpty()) {
            throw new IllegalStateException("Belum ada tiket terjual untuk undian ini.");
        }
        return daftar.get(acak.nextInt(daftar.size()));
    }
}