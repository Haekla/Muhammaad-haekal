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

public final class LaporanAdmin {

    private static final String FILE_DESAIN = "/laporan/laporan_admin.jrxml";

    private LaporanAdmin() {
    }

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

    public static void simpanPdf(JasperPrint cetak, File tujuan) throws JRException {
        JasperExportManager.exportReportToPdfFile(cetak, tujuan.getAbsolutePath());
    }

    public static void tampilkan(JasperPrint cetak) {
        JasperViewer.viewReport(cetak, false);
    }
}
