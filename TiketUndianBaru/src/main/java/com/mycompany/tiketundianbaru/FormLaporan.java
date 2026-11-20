package com.mycompany.tiketundianbaru;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * Form laporan data admin. Tabel di tengah diisi lewat Spring DAO (AdminDao.semua()),
 * lalu data yang sama dipakai JasperReports untuk membuat laporan.
 * Tombol LIHAT LAPORAN membuka pratinjau, tombol SIMPAN PDF menyimpan laporan ke file PDF.
 */
public class FormLaporan extends JFrame {

    private final DefaultTableModel modelTabel = new DefaultTableModel(new String[]{"No", "ID", "Username"}, 0) {
        @Override
        public boolean isCellEditable(int baris, int kolom) {
            return false;
        }
    };

    public FormLaporan() {
        super("Laporan Data Admin");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel judul = new JLabel("LAPORAN DATA ADMIN", JLabel.CENTER);
        judul.setFont(judul.getFont().deriveFont(java.awt.Font.BOLD, 16f));
        judul.setBorder(new EmptyBorder(16, 0, 12, 0));

        JTable tabel = new JTable(modelTabel);
        tabel.getColumnModel().getColumn(0).setMaxWidth(60);
        tabel.getColumnModel().getColumn(1).setMaxWidth(80);

        JButton tombolMuat = new JButton("MUAT ULANG");
        JButton tombolLihat = new JButton("LIHAT LAPORAN");
        JButton tombolPdf = new JButton("SIMPAN PDF");
        tombolMuat.addActionListener(e -> muatData());
        tombolLihat.addActionListener(e -> lihatLaporan());
        tombolPdf.addActionListener(e -> simpanPdf());

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 12));
        tombol.add(tombolMuat);
        tombol.add(tombolLihat);
        tombol.add(tombolPdf);

        JPanel isi = new JPanel(new BorderLayout());
        isi.setBorder(new EmptyBorder(0, 20, 0, 20));
        isi.add(judul, BorderLayout.NORTH);
        isi.add(new JScrollPane(tabel), BorderLayout.CENTER);
        isi.add(tombol, BorderLayout.SOUTH);

        setContentPane(isi);
        setSize(520, 420);
        setLocationRelativeTo(null);

        // Data dimuat setelah jendela tampil, supaya galat koneksi muncul sebagai pesan.
        SwingUtilities.invokeLater(this::muatData);
    }

    /** Mengambil semua admin lewat Spring DAO. */
    private List<Admin> ambilData() {
        return SpringKonteks.ambil(AdminDao.class).semua();
    }

    private void muatData() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            List<Admin> data = ambilData();
            modelTabel.setRowCount(0);
            int no = 1;
            for (Admin admin : data) {
                modelTabel.addRow(new Object[]{no++, admin.getId(), admin.getUsername()});
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + SpringKonteks.pesanGalat(e),
                    "Galat", JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void lihatLaporan() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            JasperPrint cetak = LaporanAdmin.susun(ambilData());
            LaporanAdmin.tampilkan(cetak);
        } catch (JRException | RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Gagal membuat laporan: " + SpringKonteks.pesanGalat(e),
                    "Galat", JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void simpanPdf() {
        JFileChooser pemilih = new JFileChooser();
        pemilih.setDialogTitle("Simpan laporan sebagai PDF");
        pemilih.setFileFilter(new FileNameExtensionFilter("Berkas PDF (*.pdf)", "pdf"));
        pemilih.setSelectedFile(new File("laporan_admin.pdf"));
        if (pemilih.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File tujuan = pemilih.getSelectedFile();
        if (!tujuan.getName().toLowerCase().endsWith(".pdf")) {
            tujuan = new File(tujuan.getParentFile(), tujuan.getName() + ".pdf");
        }
        if (tujuan.exists() && JOptionPane.showConfirmDialog(this,
                "File " + tujuan.getName() + " sudah ada. Timpa file ini?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            JasperPrint cetak = LaporanAdmin.susun(ambilData());
            LaporanAdmin.simpanPdf(cetak, tujuan);
        } catch (JRException | RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan PDF: " + SpringKonteks.pesanGalat(e),
                    "Galat", JOptionPane.ERROR_MESSAGE);
            return;
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }

        int buka = JOptionPane.showConfirmDialog(this,
                "Laporan disimpan di:\n" + tujuan.getAbsolutePath() + "\n\nBuka file sekarang?",
                "Berhasil", JOptionPane.YES_NO_OPTION);
        if (buka == JOptionPane.YES_OPTION && Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(tujuan);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "File tersimpan, tetapi tidak bisa dibuka otomatis: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            FormLaporan form = new FormLaporan();
            form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            form.setVisible(true);
        });
    }
}
