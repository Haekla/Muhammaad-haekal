package com.mycompany.tiketundianbaru;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * Form laporan. Ada tiga jenis laporan: data admin, penjualan tiket, dan daftar pemenang.
 * Tabel menampilkan data yang sama dengan isi laporan. LIHAT LAPORAN membuka pratinjau JasperReports,
 * SIMPAN PDF menyimpan laporan ke file PDF.
 */
public class FormLaporan extends FormDasar {

    private static final String JENIS_ADMIN = "Data Admin";
    private static final String JENIS_TIKET = "Penjualan Tiket";
    private static final String JENIS_PEMENANG = "Daftar Pemenang";
    private static final String SEMUA_UNDIAN = "Semua undian";

    private final JComboBox<String> pilihJenis = new JComboBox<>(new String[]{JENIS_TIKET, JENIS_PEMENANG, JENIS_ADMIN});
    private final JComboBox<Object> pilihUndian = new JComboBox<>();
    private final JLabel labelJumlah = new JLabel(" ");
    private final DefaultTableModel model = modelBaca("No");
    private final JTable tabel = new JTable(model);
    private boolean memuatPilihan;

    public FormLaporan() {
        super("Laporan", 820, 560);

        JPanel isian = new JPanel(new GridBagLayout());
        isiBaris(isian, 0, "Jenis laporan", pilihJenis);
        isiBaris(isian, 1, "Undian", pilihUndian);

        JButton tombolMuat = new JButton("MUAT ULANG");
        JButton tombolLihat = new JButton("LIHAT LAPORAN");
        JButton tombolPdf = new JButton("SIMPAN PDF");
        tombolMuat.addActionListener(e -> jalankan(this::muatPilihanUndian));
        tombolLihat.addActionListener(e -> jalankan(this::lihatLaporan));
        tombolPdf.addActionListener(e -> jalankan(this::simpanPdf));
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        tombol.add(tombolMuat);
        tombol.add(tombolLihat);
        tombol.add(tombolPdf);

        JPanel atas = new JPanel(new BorderLayout());
        atas.setBorder(new EmptyBorder(14, 20, 0, 20));
        atas.add(labelJudul("LAPORAN"), BorderLayout.NORTH);
        atas.add(isian, BorderLayout.CENTER);
        atas.add(tombol, BorderLayout.SOUTH);

        pilihJenis.addActionListener(e -> {
            pilihUndian.setEnabled(!JENIS_ADMIN.equals(pilihJenis.getSelectedItem()));
            if (!memuatPilihan) {
                jalankan(this::muatTabel);
            }
        });
        pilihUndian.addActionListener(e -> {
            if (!memuatPilihan) {
                jalankan(this::muatTabel);
            }
        });

        JPanel bawah = new JPanel(new BorderLayout());
        bawah.setBorder(new EmptyBorder(0, 20, 12, 20));
        JScrollPane gulung = new JScrollPane(tabel);
        bawah.add(gulung, BorderLayout.CENTER);
        bawah.add(labelJumlah, BorderLayout.SOUTH);

        JPanel isi = new JPanel(new BorderLayout());
        isi.add(atas, BorderLayout.NORTH);
        isi.add(bawah, BorderLayout.CENTER);
        setContentPane(isi);

        javax.swing.SwingUtilities.invokeLater(() -> jalankan(this::muatPilihanUndian));
    }

    private void muatPilihanUndian() {
        memuatPilihan = true;
        try {
            List<Object> isi = new ArrayList<>();
            isi.add(SEMUA_UNDIAN);
            isi.addAll(layanan(UndianDao.class).semua());
            pilihUndian.setModel(new DefaultComboBoxModel<>(isi.toArray()));
        } finally {
            memuatPilihan = false;
        }
        muatTabel();
    }

    /** Id undian yang dipilih, atau null untuk semua undian. */
    private Integer idUndian() {
        Object dipilih = pilihUndian.getSelectedItem();
        return dipilih instanceof Undian ? ((Undian) dipilih).getId() : null;
    }

    private List<Tiket> dataTiket() {
        Integer id = idUndian();
        return id == null ? layanan(TiketDao.class).semua() : layanan(TiketDao.class).perUndian(id);
    }

    private List<Pemenang> dataPemenang() {
        Integer id = idUndian();
        List<Pemenang> semua = layanan(PemenangDao.class).semua();
        if (id == null) {
            return semua;
        }
        List<Pemenang> saring = new ArrayList<>();
        for (Pemenang p : semua) {
            if (p.getUndian().getId().equals(id)) {
                saring.add(p);
            }
        }
        return saring;
    }

    private void muatTabel() {
        String jenis = (String) pilihJenis.getSelectedItem();
        model.setRowCount(0);
        int no = 1;
        if (JENIS_ADMIN.equals(jenis)) {
            model.setColumnIdentifiers(new String[]{"No", "ID", "Username"});
            for (Admin a : layanan(AdminDao.class).semua()) {
                model.addRow(new Object[]{no++, a.getId(), a.getUsername()});
            }
        } else if (JENIS_TIKET.equals(jenis)) {
            model.setColumnIdentifiers(new String[]{"No", "Nomor Tiket", "Undian", "Peserta", "Harga", "Waktu Beli"});
            for (Tiket t : dataTiket()) {
                model.addRow(new Object[]{no++, t.getNomor(), t.getUndian().getNama(), t.getPeserta().getNama(),
                    Format.rupiah(t.getHarga()), Format.waktu(t.getDibuatPada())});
            }
        } else {
            model.setColumnIdentifiers(new String[]{"No", "Undian", "Nomor Tiket", "Pemenang", "Hadiah", "Waktu"});
            for (Pemenang p : dataPemenang()) {
                model.addRow(new Object[]{no++, p.getUndian().getNama(), p.getTiket().getNomor(),
                    p.getPeserta().getNama(), p.getHadiah(), Format.waktu(p.getDibuatPada())});
            }
        }
        rapikan(tabel);
        labelJumlah.setText("Jumlah data: " + model.getRowCount());
    }

    private JasperPrint susun() throws JRException {
        String jenis = (String) pilihJenis.getSelectedItem();
        if (JENIS_ADMIN.equals(jenis)) {
            return LaporanJasper.susunAdmin(layanan(AdminDao.class).semua());
        }
        if (JENIS_TIKET.equals(jenis)) {
            return LaporanJasper.susunTiket(dataTiket());
        }
        return LaporanJasper.susunPemenang(dataPemenang());
    }

    private String namaFile() {
        String jenis = (String) pilihJenis.getSelectedItem();
        if (JENIS_ADMIN.equals(jenis)) {
            return "laporan_admin.pdf";
        }
        return JENIS_TIKET.equals(jenis) ? "laporan_tiket.pdf" : "laporan_pemenang.pdf";
    }

    private void lihatLaporan() throws JRException {
        LaporanJasper.tampilkan(susun());
    }

    private void simpanPdf() throws JRException {
        JFileChooser pemilih = new JFileChooser();
        pemilih.setDialogTitle("Simpan laporan sebagai PDF");
        pemilih.setFileFilter(new FileNameExtensionFilter("Berkas PDF (*.pdf)", "pdf"));
        pemilih.setSelectedFile(new File(namaFile()));
        if (pemilih.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File tujuan = pemilih.getSelectedFile();
        if (!tujuan.getName().toLowerCase().endsWith(".pdf")) {
            tujuan = new File(tujuan.getParentFile(), tujuan.getName() + ".pdf");
        }
        if (tujuan.exists() && !tanya("File " + tujuan.getName() + " sudah ada. Timpa file ini?")) {
            return;
        }

        LaporanJasper.simpanPdf(susun(), tujuan);

        int buka = JOptionPane.showConfirmDialog(this,
                "Laporan disimpan di:\n" + tujuan.getAbsolutePath() + "\n\nBuka file sekarang?",
                "Berhasil", JOptionPane.YES_NO_OPTION);
        if (buka == JOptionPane.YES_OPTION && Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(tujuan);
            } catch (IOException e) {
                peringatan("File tersimpan, tetapi tidak bisa dibuka otomatis: " + e.getMessage());
            }
        }
    }

    /** Dipakai untuk menjalankan form ini sendiri lewat Run File. */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            FormLaporan form = new FormLaporan();
            form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            form.setVisible(true);
        });
    }
}
