package com.mycompany.tiketundianbaru;

public class BarisPemenang {

    private final String undian;
    private final String nomor;
    private final String pemenang;
    private final String hadiah;
    private final String waktu;

    public BarisPemenang(String undian, String nomor, String pemenang, String hadiah, String waktu) {
        this.undian = undian;
        this.nomor = nomor;
        this.pemenang = pemenang;
        this.hadiah = hadiah;
        this.waktu = waktu;
    }

    public String getUndian() {
        return undian;
    }

    public String getNomor() {
        return nomor;
    }

    public String getPemenang() {
        return pemenang;
    }

    public String getHadiah() {
        return hadiah;
    }

    public String getWaktu() {
        return waktu;
    }
}