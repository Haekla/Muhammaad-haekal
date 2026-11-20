package com.mycompany.tiketundianbaru;

import java.io.File;
import java.util.List;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * Program kecil untuk menguji JasperReports tanpa tampilan.
 * Jalankan lewat klik kanan file ini, lalu Run File. Hasilnya tampil di jendela Output NetBeans
 * dan file laporan_admin_uji.pdf dibuat di folder project.
 */
public class UjiLaporan {

    public static void main(String[] args) {
        try {
            System.out.println("1. Mengambil data admin lewat Spring DAO...");
            List<Admin> data = SpringKonteks.ambil(AdminDao.class).semua();
            System.out.println("   Berhasil. Jumlah admin: " + data.size());

            System.out.println("2. JasperReports menyusun laporan dari laporan_admin.jrxml...");
            JasperPrint cetak = LaporanAdmin.susun(data);
            System.out.println("   Berhasil. Jumlah halaman: " + cetak.getPages().size());

            System.out.println("3. Mengekspor laporan ke PDF...");
            File tujuan = new File("laporan_admin_uji.pdf");
            LaporanAdmin.simpanPdf(cetak, tujuan);
            System.out.println("   Berhasil. File: " + tujuan.getAbsolutePath()
                    + " (" + tujuan.length() + " byte)");
        } catch (Exception e) {
            System.out.println("GAGAL: " + SpringKonteks.pesanGalat(e));
            e.printStackTrace();
        } finally {
            SpringKonteks.tutup();
        }
    }
}
