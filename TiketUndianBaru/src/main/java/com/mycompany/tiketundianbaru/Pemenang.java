package com.mycompany.tiketundianbaru;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pemenang")
public class Pemenang extends Entitas {

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "undian_id", nullable = false)
    private Undian undian;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tiket_id", nullable = false)
    private Tiket tiket;

    @Column(name = "hadiah", nullable = false, length = 150)
    private String hadiah;
    protected Pemenang() {
    }

    public Pemenang(Undian undian, Tiket tiket, String hadiah) {
        setUndian(undian);
        setTiket(tiket);
        setHadiah(hadiah);
    }

    public Undian getUndian() {
        return undian;
    }

    protected void setUndian(Undian undian) {
        if (undian == null) {
            throw new IllegalArgumentException("Undian wajib dipilih.");
        }
        this.undian = undian;
    }

    public Tiket getTiket() {
        return tiket;
    }

    protected void setTiket(Tiket tiket) {
        if (tiket == null) {
            throw new IllegalArgumentException("Tiket pemenang wajib ada.");
        }
        this.tiket = tiket;
    }

    public String getHadiah() {
        return hadiah;
    }

    protected void setHadiah(String hadiah) {
        this.hadiah = wajibIsi(hadiah, "Hadiah", 150);
    }

    public Peserta getPeserta() {
        return tiket.getPeserta();
    }
}