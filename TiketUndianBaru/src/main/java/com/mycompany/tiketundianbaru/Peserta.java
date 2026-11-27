package com.mycompany.tiketundianbaru;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "peserta")
public class Peserta extends Entitas {

    @Column(name = "nama", nullable = false, length = 100)
    private String nama;

    @Column(name = "no_hp", nullable = false, length = 20)
    private String noHp;

    @Column(name = "alamat", length = 150)
    private String alamat;

    protected Peserta() {
    }

    public Peserta(String nama, String noHp, String alamat) {
        setNama(nama);
        setNoHp(noHp);
        setAlamat(alamat);
    }

    public String getNama() {
        return nama;
    }

    protected void setNama(String nama) {
        this.nama = wajibIsi(nama, "Nama peserta", 100);
    }

    public String getNoHp() {
        return noHp;
    }

    protected void setNoHp(String noHp) {
        String angka = noHp == null ? "" : noHp.replaceAll("[\\s-]", "");
        if (!angka.matches("\\d{8,15}")) {
            throw new IllegalArgumentException("No HP harus 8 sampai 15 angka.");
        }
        this.noHp = angka;
    }

    public String getAlamat() {
        return alamat;
    }

    protected void setAlamat(String alamat) {
        this.alamat = opsional(alamat, "Alamat", 150);
    }

    @Override
    public String toString() {
        return nama + " (" + noHp + ")";
    }
}