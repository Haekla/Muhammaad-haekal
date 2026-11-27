package com.mycompany.tiketundianbaru;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/** Kelola data undian: tambah, ubah, hapus, dan lihat daftar. */
public class FormUndian extends FormDasar {

    private final JTextField kolomNama = new JTextField(20);
    private final JTextField kolomHarga = new JTextField(20);
    private final JTextField kolomHadiah = new JTextField(20);
    private final JTextField kolomTanggal = new JTextField(20);
    private final DefaultTableModel model = modelBaca("ID", "Nama Undian", "Harga Tiket", "Hadiah", "Tanggal Undi", "Status");
    private final JTable tabel = new JTable(model);
    private List<Undian> daftar = new ArrayList<>();

    public FormUndian() {
        super("Kelola Undian", 760, 540);

        JPanel isian = new JPanel(new GridBagLayout());
        isiBaris(isian, 0, "Nama undian", kolomNama);
        isiBaris(isian, 1, "Harga tiket (Rp)", kolomHarga);
        isiBaris(isian, 2, "Hadiah", kolomHadiah);
        isiBaris(isian, 3, "Tanggal undi (yyyy-MM-dd)", kolomTanggal);

        JButton tombolTambah = new JButton("TAMBAH");
        JButton tombolUbah = new JButton("UBAH");
        JButton tombolHapus = new JButton("HAPUS");
        JButton tombolBersih = new JButton("BERSIHKAN");
        tombolTambah.addActionListener(e -> jalankan(this::tambah));
        tombolUbah.addActionListener(e -> jalankan(this::ubah));
        tombolHapus.addActionListener(e -> jalankan(this::hapus));
        tombolBersih.addActionListener(e -> bersihkan());
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        tombol.add(tombolTambah);
        tombol.add(tombolUbah);
        tombol.add(tombolHapus);
        tombol.add(tombolBersih);

        JPanel atas = new JPanel(new BorderLayout());
        atas.setBorder(new EmptyBorder(14, 20, 0, 20));
        atas.add(labelJudul("KELOLA UNDIAN"), BorderLayout.NORTH);
        atas.add(isian, BorderLayout.CENTER);
        atas.add(tombol, BorderLayout.SOUTH);

        rapikan(tabel);
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                isiDariBaris();
            }
        });

        JPanel isi = new JPanel(new BorderLayout());
        isi.add(atas, BorderLayout.NORTH);
        JScrollPane gulung = new JScrollPane(tabel);
        gulung.setBorder(new EmptyBorder(0, 20, 20, 20));
        isi.add(gulung, BorderLayout.CENTER);
        setContentPane(isi);
        getRootPane().setDefaultButton(tombolTambah);

        // Data dimuat setelah jendela tampil, supaya galat koneksi muncul sebagai pesan.
        javax.swing.SwingUtilities.invokeLater(() -> jalankan(this::muat));
    }

    private void muat() {
        daftar = layanan(UndianDao.class).semua();
        model.setRowCount(0);
        for (Undian u : daftar) {
            model.addRow(new Object[]{u.getId(), u.getNama(), Format.rupiah(u.getHargaTiket()), u.getHadiah(),
                Format.tanggal(u.getTanggalUndi()), u.getStatus()});
        }
    }

    private Undian terpilih() {
        int baris = tabel.getSelectedRow();
        return baris < 0 || baris >= daftar.size() ? null : daftar.get(baris);
    }

    private void isiDariBaris() {
        Undian u = terpilih();
        if (u != null) {
            kolomNama.setText(u.getNama());
            kolomHarga.setText(String.valueOf(u.getHargaTiket()));
            kolomHadiah.setText(u.getHadiah());
            kolomTanggal.setText(u.getTanggalUndi().toString());
        }
    }

    private long bacaHarga() {
        String angka = kolomHarga.getText().replaceAll("[^0-9]", "");
        if (angka.isEmpty()) {
            throw new IllegalArgumentException("Harga tiket wajib diisi dengan angka.");
        }
        try {
            return Long.parseLong(angka);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Harga tiket terlalu besar.");
        }
    }

    private LocalDate bacaTanggal() {
        try {
            return LocalDate.parse(kolomTanggal.getText().trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Tanggal undi harus berformat yyyy-MM-dd, contoh 2026-12-31.");
        }
    }

    private void tambah() {
        Undian baru = new Undian(kolomNama.getText(), bacaHarga(), kolomHadiah.getText(), bacaTanggal());
        layanan(UndianDao.class).simpan(baru);
        info("Undian " + baru.getNama() + " berhasil disimpan.");
        bersihkan();
        muat();
    }

    private void ubah() {
        Undian pilihan = terpilih();
        if (pilihan == null) {
            throw new IllegalArgumentException("Pilih undian di tabel dulu.");
        }
        UndianDao dao = layanan(UndianDao.class);
        Undian undian = dao.cari(pilihan.getId());
        if (undian == null) {
            throw new IllegalStateException("Undian sudah tidak ada di database. Muat ulang daftar.");
        }
        if (!undian.isBuka()) {
            throw new IllegalStateException("Undian yang sudah selesai tidak bisa diubah.");
        }
        undian.setNama(kolomNama.getText());
        undian.setHargaTiket(bacaHarga());
        undian.setHadiah(kolomHadiah.getText());
        undian.setTanggalUndi(bacaTanggal());
        dao.simpan(undian);
        info("Undian berhasil diubah.");
        muat();
    }

    private void hapus() {
        Undian pilihan = terpilih();
        if (pilihan == null) {
            throw new IllegalArgumentException("Pilih undian di tabel dulu.");
        }
        if (layanan(TiketDao.class).jumlahPerUndian(pilihan.getId()) > 0) {
            throw new IllegalStateException("Undian " + pilihan.getNama() + " sudah punya tiket, jadi tidak bisa dihapus.");
        }
        if (tanya("Hapus undian " + pilihan.getNama() + "?")) {
            layanan(UndianDao.class).hapus(pilihan.getId());
            bersihkan();
            muat();
        }
    }

    private void bersihkan() {
        kolomNama.setText("");
        kolomHarga.setText("");
        kolomHadiah.setText("");
        kolomTanggal.setText("");
        tabel.clearSelection();
        kolomNama.requestFocusInWindow();
    }

    /** Dipakai untuk menjalankan form ini sendiri lewat Run File. */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            FormUndian form = new FormUndian();
            form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            form.setVisible(true);
        });
    }
}
