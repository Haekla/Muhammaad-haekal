package com.mycompany.tiketundianbaru;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class Entitas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "dibuat_pada", nullable = false)
    private LocalDateTime dibuatPada;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDibuatPada() {
        return dibuatPada;
    }

    protected void setDibuatPada(LocalDateTime dibuatPada) {
        this.dibuatPada = dibuatPada;
    }

    @PrePersist
    protected void sebelumDisimpan() {
        if (dibuatPada == null) {
            dibuatPada = LocalDateTime.now();
        }
    }

    protected static String wajibIsi(String nilai, String namaKolom, int maksimal) {
        String bersih = nilai == null ? "" : nilai.trim();
        if (bersih.isEmpty()) {
            throw new IllegalArgumentException(namaKolom + " wajib diisi.");
        }
        if (bersih.length() > maksimal) {
            throw new IllegalArgumentException(namaKolom + " maksimal " + maksimal + " karakter.");
        }
        return bersih;
    }

    protected static String opsional(String nilai, String namaKolom, int maksimal) {
        String bersih = nilai == null ? "" : nilai.trim();
        if (bersih.length() > maksimal) {
            throw new IllegalArgumentException(namaKolom + " maksimal " + maksimal + " karakter.");
        }
        return bersih;
    }
}