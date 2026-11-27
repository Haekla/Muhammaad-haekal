package com.mycompany.tiketundianbaru;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Pusat laporan JasperReports untuk tiga laporan: data admin, penjualan tiket, dan daftar pemenang.
 * Setiap laporan punya file desain .jrxml di src/main/resources/laporan.
 * Data entitas diubah dulu menjadi baris teks sederhana (BarisTiket, BarisPemenang)
 * agar laporan tidak bergantung pada koneksi database.
 */
public final class LaporanJasper {

    private LaporanJasper() {
    }

    public static JasperPrint susunAdmin(List<Admin> data) throws JRException {
        return LaporanAdmin.susun(data);
    }

    public static JasperPrint susunTiket(List<Tiket> data) throws JRException {
        List<BarisTiket> baris = new ArrayList<>();
        long total = 0;
        for (Tiket t : data) {
            baris.add(new BarisTiket(t.getNomor(), t.getUndian().getNama(), t.getPeserta().getNama(),
                    Format.rupiah(t.getHarga()), Format.waktu(t.getDibuatPada())));
            total += t.getHarga();
        }
        Map<String, Object> parameter = parameterDasar("LAPORAN PENJUALAN TIKET");
        parameter.put("JUMLAH", String.valueOf(data.size()));
        parameter.put("TOTAL", Format.rupiah(total));
        return isi("/laporan/laporan_tiket.jrxml", parameter, baris);
    }

    public static JasperPrint susunPemenang(List<Pemenang> data) throws JRException {
        List<BarisPemenang> baris = new ArrayList<>();
        for (Pemenang p : data) {
            baris.add(new BarisPemenang(p.getUndian().getNama(), p.getTiket().getNomor(),
                    p.getPeserta().getNama(), p.getHadiah(), Format.waktu(p.getDibuatPada())));
        }
        return isi("/laporan/laporan_pemenang.jrxml", parameterDasar("LAPORAN DAFTAR PEMENANG"), baris);
    }

    public static void simpanPdf(JasperPrint cetak, File tujuan) throws JRException {
        LaporanAdmin.simpanPdf(cetak, tujuan);
    }

    public static void tampilkan(JasperPrint cetak) {
        LaporanAdmin.tampilkan(cetak);
    }

    private static Map<String, Object> parameterDasar(String judul) {
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("JUDUL", judul);
        parameter.put("DICETAK", "Dicetak: " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()));
        return parameter;
    }

    /** Tahap compile dan fill: desain .jrxml diubah menjadi laporan lalu diisi data. */
    private static JasperPrint isi(String desain, Map<String, Object> parameter, Collection<?> data)
            throws JRException {
        try (InputStream masuk = LaporanJasper.class.getResourceAsStream(desain)) {
            if (masuk == null) {
                throw new JRException("File desain laporan tidak ditemukan: " + desain
                        + ". Jalankan Clean and Build lalu coba lagi.");
            }
            JasperReport laporan = JasperCompileManager.compileReport(masuk);
            return JasperFillManager.fillReport(laporan, parameter, new JRBeanCollectionDataSource(data));
        } catch (IOException e) {
            throw new JRException(e);
        }
    }
}
