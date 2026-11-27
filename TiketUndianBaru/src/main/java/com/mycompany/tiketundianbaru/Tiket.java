package com.mycompany.tiketundianbaru;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tiket")
public class Tiket extends Entitas {

    @Column(name = "nomor", nullable = false, length = 30)
    private String nomor;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "undian_id", nullable = false)
    private Undian undian;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "peserta_id", nullable = false)
    private Peserta peserta;

    @Column(name = "harga", nullable = false)
    private long harga;

    protected Tiket() {
    }

    public Tiket(String nomor, Undian undian, Peserta peserta, long harga) {
        setNomor(nomor);
        setUndian(undian);
        setPeserta(peserta);
        setHarga(harga);
    }

    public String getNomor() {
        return nomor;
    }

    protected void setNomor(String nomor) {
        this.nomor = wajibIsi(nomor, "Nomor tiket", 30);
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

    public Peserta getPeserta() {
        return peserta;
    }

    protected void setPeserta(Peserta peserta) {
        if (peserta == null) {
            throw new IllegalArgumentException("Peserta wajib dipilih.");
        }
        this.peserta = peserta;
    }

    public long getHarga() {
        return harga;
    }

    protected void setHarga(long harga) {
        if (harga <= 0) {
            throw new IllegalArgumentException("Harga tiket harus lebih dari 0.");
        }
        this.harga = harga;
    }

    @Override
    public String toString() {
        return nomor;
    }
}