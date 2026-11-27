package com.mycompany.tiketundianbaru;

public class BarisTiket {

    private final String nomor;
    private final String undian;
    private final String peserta;
    private final String harga;
    private final String waktu;

    public BarisTiket(String nomor, String undian, String peserta, String harga, String waktu) {
        this.nomor = nomor;
        this.undian = undian;
        this.peserta = peserta;
        this.harga = harga;
        this.waktu = waktu;
    }

    public String getNomor() {
        return nomor;
    }

    public String getUndian() {
        return undian;
    }

    public String getPeserta() {
        return peserta;
    }

    public String getHarga() {
        return harga;
    }

    public String getWaktu() {
        return waktu;
    }
}