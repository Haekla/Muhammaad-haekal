package com.mycompany.tiketundianbaru;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Penghubung antara aplikasi dan JasperReports. Alurnya tiga tahap:
 * 1. compile: file desain laporan (.jrxml) diubah menjadi objek JasperReport.
 * 2. fill: laporan diisi dengan data dari database, hasilnya JasperPrint.
 * 3. export atau view: JasperPrint disimpan sebagai PDF atau ditampilkan di jendela pratinjau.
 */
public final class LaporanAdmin {

    /** Lokasi desain laporan di dalam folder src/main/resources. */
    private static final String FILE_DESAIN = "/laporan/laporan_admin.jrxml";

    private LaporanAdmin() {
    }

    /** Menyusun laporan dari daftar admin. Data diambil lewat AdminDao, bukan lewat SQL di sini. */
    public static JasperPrint susun(List<Admin> data) throws JRException {
        try (InputStream desain = LaporanAdmin.class.getResourceAsStream(FILE_DESAIN)) {
            if (desain == null) {
                throw new JRException("File desain laporan tidak ditemukan: " + FILE_DESAIN
                        + ". Jalankan Clean and Build lalu coba lagi.");
            }
            JasperReport laporan = JasperCompileManager.compileReport(desain);

            Map<String, Object> parameter = new HashMap<>();
            parameter.put("JUDUL", "LAPORAN DATA ADMIN");
            parameter.put("DICETAK", "Dicetak: " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()));

            return JasperFillManager.fillReport(laporan, parameter, new JRBeanCollectionDataSource(data));
        } catch (IOException e) {
            throw new JRException(e);
        }
    }

    /** Menyimpan laporan sebagai file PDF. */
    public static void simpanPdf(JasperPrint cetak, File tujuan) throws JRException {
        JasperExportManager.exportReportToPdfFile(cetak, tujuan.getAbsolutePath());
    }

    /** Menampilkan pratinjau laporan. Dari jendela ini laporan juga bisa dicetak atau disimpan. */
    public static void tampilkan(JasperPrint cetak) {
        JasperViewer.viewReport(cetak, false);
    }
}
