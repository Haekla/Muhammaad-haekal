package com.mycompany.tiketundianbaru;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
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

/** Kelola data peserta: tambah, ubah, hapus, dan lihat daftar. */
public class FormPeserta extends FormDasar {

    private final JTextField kolomNama = new JTextField(20);
    private final JTextField kolomNoHp = new JTextField(20);
    private final JTextField kolomAlamat = new JTextField(20);
    private final DefaultTableModel model = modelBaca("ID", "Nama", "No HP", "Alamat");
    private final JTable tabel = new JTable(model);
    private List<Peserta> daftar = new ArrayList<>();

    public FormPeserta() {
        super("Kelola Peserta", 700, 520);

        JPanel isian = new JPanel(new GridBagLayout());
        isiBaris(isian, 0, "Nama", kolomNama);
        isiBaris(isian, 1, "No HP", kolomNoHp);
        isiBaris(isian, 2, "Alamat", kolomAlamat);

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
        atas.add(labelJudul("KELOLA PESERTA"), BorderLayout.NORTH);
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

        javax.swing.SwingUtilities.invokeLater(() -> jalankan(this::muat));
    }

    private void muat() {
        daftar = layanan(PesertaDao.class).semua();
        model.setRowCount(0);
        for (Peserta p : daftar) {
            model.addRow(new Object[]{p.getId(), p.getNama(), p.getNoHp(), p.getAlamat()});
        }
    }

    private Peserta terpilih() {
        int baris = tabel.getSelectedRow();
        return baris < 0 || baris >= daftar.size() ? null : daftar.get(baris);
    }

    private void isiDariBaris() {
        Peserta p = terpilih();
        if (p != null) {
            kolomNama.setText(p.getNama());
            kolomNoHp.setText(p.getNoHp());
            kolomAlamat.setText(p.getAlamat());
        }
    }

    private void tambah() {
        Peserta baru = new Peserta(kolomNama.getText(), kolomNoHp.getText(), kolomAlamat.getText());
        layanan(PesertaDao.class).simpan(baru);
        info("Peserta " + baru.getNama() + " berhasil disimpan.");
        bersihkan();
        muat();
    }

    private void ubah() {
        Peserta pilihan = terpilih();
        if (pilihan == null) {
            throw new IllegalArgumentException("Pilih peserta di tabel dulu.");
        }
        PesertaDao dao = layanan(PesertaDao.class);
        Peserta peserta = dao.cari(pilihan.getId());
        if (peserta == null) {
            throw new IllegalStateException("Peserta sudah tidak ada di database. Muat ulang daftar.");
        }
        peserta.setNama(kolomNama.getText());
        peserta.setNoHp(kolomNoHp.getText());
        peserta.setAlamat(kolomAlamat.getText());
        dao.simpan(peserta);
        info("Peserta berhasil diubah.");
        muat();
    }

    private void hapus() {
        Peserta pilihan = terpilih();
        if (pilihan == null) {
            throw new IllegalArgumentException("Pilih peserta di tabel dulu.");
        }
        if (layanan(TiketDao.class).jumlahPerPeserta(pilihan.getId()) > 0) {
            throw new IllegalStateException("Peserta " + pilihan.getNama() + " sudah punya tiket, jadi tidak bisa dihapus.");
        }
        if (tanya("Hapus peserta " + pilihan.getNama() + "?")) {
            layanan(PesertaDao.class).hapus(pilihan.getId());
            bersihkan();
            muat();
        }
    }

    private void bersihkan() {
        kolomNama.setText("");
        kolomNoHp.setText("");
        kolomAlamat.setText("");
        tabel.clearSelection();
        kolomNama.requestFocusInWindow();
    }

    /** Dipakai untuk menjalankan form ini sendiri lewat Run File. */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            FormPeserta form = new FormPeserta();
            form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            form.setVisible(true);
        });
    }
}
