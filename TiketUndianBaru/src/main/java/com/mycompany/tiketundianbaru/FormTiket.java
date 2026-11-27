package com.mycompany.tiketundianbaru;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class FormTiket extends FormDasar {

    private final JComboBox<Undian> pilihUndian = new JComboBox<>();
    private final JComboBox<Peserta> pilihPeserta = new JComboBox<>();
    private final JSpinner kolomJumlah = new JSpinner(new SpinnerNumberModel(1, 1, LayananUndian.MAKS_TIKET_SEKALI_JUAL, 1));
    private final JLabel labelHarga = new JLabel("Harga satuan: Rp 0");
    private final JLabel labelTotal = new JLabel("Total bayar: Rp 0");
    private final DefaultTableModel model = modelBaca("No", "Nomor Tiket", "Peserta", "Harga", "Waktu Beli");
    private final JTable tabel = new JTable(model);
    private boolean memuatPilihan;

    public FormTiket() {
        super("Jual Tiket", 760, 560);

        JPanel isian = new JPanel(new GridBagLayout());
        isiBaris(isian, 0, "Undian", pilihUndian);
        isiBaris(isian, 1, "Peserta", pilihPeserta);
        isiBaris(isian, 2, "Jumlah tiket", kolomJumlah);
        isiBaris(isian, 3, "", labelHarga);
        isiBaris(isian, 4, "", labelTotal);

        JButton tombolJual = new JButton("JUAL TIKET");
        JButton tombolMuat = new JButton("MUAT ULANG");
        tombolJual.addActionListener(e -> jalankan(this::jual));
        tombolMuat.addActionListener(e -> jalankan(this::muat));
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        tombol.add(tombolJual);
        tombol.add(tombolMuat);

        JPanel atas = new JPanel(new BorderLayout());
        atas.setBorder(new EmptyBorder(14, 20, 0, 20));
        atas.add(labelJudul("JUAL TIKET"), BorderLayout.NORTH);
        atas.add(isian, BorderLayout.CENTER);
        atas.add(tombol, BorderLayout.SOUTH);

        rapikan(tabel);
        pilihUndian.addActionListener(e -> {
            if (!memuatPilihan) {
                hitungTotal();
                jalankan(this::muatTabel);
            }
        });
        kolomJumlah.addChangeListener(e -> hitungTotal());

        JPanel isi = new JPanel(new BorderLayout());
        isi.add(atas, BorderLayout.NORTH);
        JScrollPane gulung = new JScrollPane(tabel);
        gulung.setBorder(new EmptyBorder(0, 20, 20, 20));
        isi.add(gulung, BorderLayout.CENTER);
        setContentPane(isi);
        getRootPane().setDefaultButton(tombolJual);

        javax.swing.SwingUtilities.invokeLater(() -> jalankan(this::muat));
    }

    private void muat() {
        memuatPilihan = true;
        try {
            Undian dipilih = (Undian) pilihUndian.getSelectedItem();
            List<Undian> daftarUndian = layanan(UndianDao.class).semuaBuka();
            pilihUndian.setModel(new DefaultComboBoxModel<>(daftarUndian.toArray(new Undian[0])));
            if (dipilih != null) {
                for (Undian u : daftarUndian) {
                    if (u.getId().equals(dipilih.getId())) {
                        pilihUndian.setSelectedItem(u);
                    }
                }
            }
            List<Peserta> daftarPeserta = layanan(PesertaDao.class).semua();
            pilihPeserta.setModel(new DefaultComboBoxModel<>(daftarPeserta.toArray(new Peserta[0])));
        } finally {
            memuatPilihan = false;
        }
        hitungTotal();
        muatTabel();
    }

    private void muatTabel() {
        model.setRowCount(0);
        Undian undian = (Undian) pilihUndian.getSelectedItem();
        if (undian == null) {
            return;
        }
        List<Tiket> daftar = new ArrayList<>(layanan(TiketDao.class).perUndian(undian.getId()));
        int no = 1;
        for (Tiket t : daftar) {
            model.addRow(new Object[]{no++, t.getNomor(), t.getPeserta().getNama(),
                Format.rupiah(t.getHarga()), Format.waktu(t.getDibuatPada())});
        }
    }

    private int bacaJumlah() {
        try {
            kolomJumlah.commitEdit();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Jumlah tiket harus berupa angka.");
        }
        return (Integer) kolomJumlah.getValue();
    }

    private void hitungTotal() {
        Undian undian = (Undian) pilihUndian.getSelectedItem();
        long harga = undian == null ? 0 : undian.getHargaTiket();
        int jumlah = (Integer) kolomJumlah.getValue();
        labelHarga.setText("Harga satuan: " + Format.rupiah(harga));
        labelTotal.setText("Total bayar: " + Format.rupiah(harga * jumlah));
    }

    private void jual() {
        Undian undian = (Undian) pilihUndian.getSelectedItem();
        Peserta peserta = (Peserta) pilihPeserta.getSelectedItem();
        if (undian == null) {
            throw new IllegalArgumentException("Belum ada undian yang dibuka. Tambah undian dulu di menu Kelola Undian.");
        }
        if (peserta == null) {
            throw new IllegalArgumentException("Belum ada peserta. Tambah peserta dulu di menu Kelola Peserta.");
        }
        int jumlah = bacaJumlah();
        List<Tiket> hasil = layanan(LayananUndian.class).jualTiket(undian.getId(), peserta.getId(), jumlah);
        long total = 0;
        for (Tiket t : hasil) {
            total += t.getHarga();
        }
        info(hasil.size() + " tiket terjual untuk " + peserta.getNama() + ".\n"
                + "Nomor: " + hasil.get(0).getNomor()
                + (hasil.size() > 1 ? " sampai " + hasil.get(hasil.size() - 1).getNomor() : "") + "\n"
                + "Total: " + Format.rupiah(total));
        muatTabel();
    }

    /** Dipakai untuk menjalankan form ini sendiri lewat Run File. */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            FormTiket form = new FormTiket();
            form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            form.setVisible(true);
        });
    }
}
