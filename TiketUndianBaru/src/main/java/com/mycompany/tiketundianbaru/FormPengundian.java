package com.mycompany.tiketundianbaru;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.Random;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/** Pengundian pemenang. Pemenang dipilih acak oleh LayananUndian, lalu form menampilkan hasilnya. */
public class FormPengundian extends FormDasar {

    private final JComboBox<Undian> pilihUndian = new JComboBox<>();
    private final JLabel labelInfo = new JLabel(" ", JLabel.CENTER);
    private final JLabel labelNomor = new JLabel("-", JLabel.CENTER);
    private final JLabel labelPemenang = new JLabel(" ", JLabel.CENTER);
    private final JButton tombolUndi = new JButton("UNDI PEMENANG");
    private final DefaultTableModel model = modelBaca("No", "Undian", "Nomor Tiket", "Pemenang", "Hadiah", "Waktu");
    private final JTable tabel = new JTable(model);
    private boolean memuatPilihan;

    public FormPengundian() {
        super("Undi Pemenang", 760, 580);

        labelNomor.setFont(labelNomor.getFont().deriveFont(Font.BOLD, 36f));
        labelPemenang.setFont(labelPemenang.getFont().deriveFont(Font.BOLD, 16f));

        JPanel pilih = new JPanel(new BorderLayout(10, 0));
        pilih.add(new JLabel("Undian"), BorderLayout.WEST);
        pilih.add(pilihUndian, BorderLayout.CENTER);
        pilih.add(tombolUndi, BorderLayout.EAST);

        JPanel hasil = new JPanel(new GridLayout(3, 1, 0, 6));
        hasil.setBorder(new EmptyBorder(10, 0, 10, 0));
        hasil.add(labelInfo);
        hasil.add(labelNomor);
        hasil.add(labelPemenang);

        JPanel atas = new JPanel(new BorderLayout());
        atas.setBorder(new EmptyBorder(14, 20, 0, 20));
        JPanel judul = new JPanel(new BorderLayout(0, 10));
        judul.add(labelJudul("UNDI PEMENANG"), BorderLayout.NORTH);
        judul.add(pilih, BorderLayout.SOUTH);
        atas.add(judul, BorderLayout.NORTH);
        atas.add(hasil, BorderLayout.CENTER);
        atas.add(new JLabel("Riwayat pemenang"), BorderLayout.SOUTH);

        rapikan(tabel);
        tombolUndi.addActionListener(e -> jalankan(this::undi));
        pilihUndian.addActionListener(e -> {
            if (!memuatPilihan) {
                jalankan(this::tampilkanInfo);
            }
        });

        JPanel isi = new JPanel(new BorderLayout());
        isi.add(atas, BorderLayout.NORTH);
        JScrollPane gulung = new JScrollPane(tabel);
        gulung.setBorder(new EmptyBorder(4, 20, 20, 20));
        isi.add(gulung, BorderLayout.CENTER);
        setContentPane(isi);

        javax.swing.SwingUtilities.invokeLater(() -> jalankan(this::muat));
    }

    private void muat() {
        memuatPilihan = true;
        try {
            List<Undian> daftar = layanan(UndianDao.class).semuaBuka();
            pilihUndian.setModel(new DefaultComboBoxModel<>(daftar.toArray(new Undian[0])));
        } finally {
            memuatPilihan = false;
        }
        tombolUndi.setEnabled(pilihUndian.getItemCount() > 0);
        tampilkanInfo();

        model.setRowCount(0);
        int no = 1;
        for (Pemenang p : layanan(PemenangDao.class).semua()) {
            model.addRow(new Object[]{no++, p.getUndian().getNama(), p.getTiket().getNomor(),
                p.getPeserta().getNama(), p.getHadiah(), Format.waktu(p.getDibuatPada())});
        }
    }

    private void tampilkanInfo() {
        Undian undian = (Undian) pilihUndian.getSelectedItem();
        if (undian == null) {
            labelInfo.setText("Tidak ada undian yang terbuka.");
            return;
        }
        long jumlah = layanan(TiketDao.class).jumlahPerUndian(undian.getId());
        labelInfo.setText("Hadiah: " + undian.getHadiah() + "  |  Tiket terjual: " + jumlah
                + "  |  Tanggal undi: " + Format.tanggal(undian.getTanggalUndi()));
    }

    private void undi() {
        Undian undian = (Undian) pilihUndian.getSelectedItem();
        if (undian == null) {
            throw new IllegalArgumentException("Tidak ada undian yang terbuka.");
        }
        List<Tiket> semuaTiket = layanan(TiketDao.class).perUndian(undian.getId());
        if (semuaTiket.isEmpty()) {
            throw new IllegalStateException("Belum ada tiket terjual untuk undian " + undian.getNama() + ".");
        }
        if (!tanya("Undi pemenang untuk " + undian.getNama() + "?\nUndian ditutup setelah diundi.")) {
            return;
        }
        Pemenang pemenang = layanan(LayananUndian.class).undi(undian.getId());
        animasi(pemenang, semuaTiket);
    }

    /** Menampilkan nomor tiket yang berganti cepat, lalu berhenti di pemenang yang sudah ditentukan. */
    private void animasi(Pemenang pemenang, List<Tiket> semuaTiket) {
        tombolUndi.setEnabled(false);
        labelPemenang.setText(" ");
        Random acak = new Random();
        int[] putaran = {0};
        Timer timer = new Timer(80, null);
        timer.addActionListener(e -> {
            putaran[0]++;
            if (putaran[0] < 20) {
                labelNomor.setText(semuaTiket.get(acak.nextInt(semuaTiket.size())).getNomor());
                return;
            }
            timer.stop();
            labelNomor.setText(pemenang.getTiket().getNomor());
            labelPemenang.setText("Pemenang " + pemenang.getUndian().getNama() + ": " + pemenang.getPeserta().getNama());
            jalankan(this::muat);
            info("Pemenang undian " + pemenang.getUndian().getNama() + ":\n"
                    + pemenang.getPeserta().getNama() + " (" + pemenang.getPeserta().getNoHp() + ")\n"
                    + "Tiket " + pemenang.getTiket().getNomor() + "\nHadiah: " + pemenang.getHadiah());
        });
        timer.start();
    }

    /** Dipakai untuk menjalankan form ini sendiri lewat Run File. */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            FormPengundian form = new FormPengundian();
            form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            form.setVisible(true);
        });
    }
}
