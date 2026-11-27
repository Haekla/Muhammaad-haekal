package com.mycompany.tiketundianbaru;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "undian")
public class Undian extends Entitas {

    public static final String STATUS_BUKA = "BUKA";
    public static final String STATUS_SELESAI = "SELESAI";

    @Column(name = "nama", nullable = false, length = 100)
    private String nama;

    @Column(name = "harga_tiket", nullable = false)
    private long hargaTiket;

    @Column(name = "hadiah", nullable = false, length = 150)
    private String hadiah;

    @Column(name = "tanggal_undi", nullable = false)
    private LocalDate tanggalUndi;

    @Column(name = "status", nullable = false, length = 10)
    private String status = STATUS_BUKA;

    protected Undian() {
    }

    public Undian(String nama, long hargaTiket, String hadiah, LocalDate tanggalUndi) {
        setNama(nama);
        setHargaTiket(hargaTiket);
        setHadiah(hadiah);
        setTanggalUndi(tanggalUndi);
    }

    public String getNama() {
        return nama;
    }

    protected void setNama(String nama) {
        this.nama = wajibIsi(nama, "Nama undian", 100);
    }

    public long getHargaTiket() {
        return hargaTiket;
    }

    protected void setHargaTiket(long hargaTiket) {
        if (hargaTiket <= 0) {
            throw new IllegalArgumentException("Harga tiket harus lebih dari 0.");
        }
        this.hargaTiket = hargaTiket;
    }

    public String getHadiah() {
        return hadiah;
    }

    protected void setHadiah(String hadiah) {
        this.hadiah = wajibIsi(hadiah, "Hadiah", 150);
    }

    public LocalDate getTanggalUndi() {
        return tanggalUndi;
    }

    protected void setTanggalUndi(LocalDate tanggalUndi) {
        if (tanggalUndi == null) {
            throw new IllegalArgumentException("Tanggal undi wajib diisi.");
        }
        this.tanggalUndi = tanggalUndi;
    }

    public String getStatus() {
        return status;
    }

    protected void setStatus(String status) {
        if (!STATUS_BUKA.equals(status) && !STATUS_SELESAI.equals(status)) {
            throw new IllegalArgumentException("Status undian harus BUKA atau SELESAI.");
        }
        this.status = status;
    }

    public boolean isBuka() {
        return STATUS_BUKA.equals(status);
    }

    @Override
    public String toString() {
        return nama;
    }
}