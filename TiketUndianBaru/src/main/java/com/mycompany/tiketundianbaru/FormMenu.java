package com.mycompany.tiketundianbaru;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class FormMenu extends FormDasar {

    private final JLabel nilaiUndian = kartuNilai();
    private final JLabel nilaiPeserta = kartuNilai();
    private final JLabel nilaiTiket = kartuNilai();
    private final JLabel nilaiPendapatan = kartuNilai();

    public FormMenu() {
        this("admin", true);
    }

    public FormMenu(String namaAdmin) {
        this(namaAdmin, true);
    }

    public FormMenu(String namaAdmin, boolean admin) {
        super("Aplikasi Tiket Undian", 640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel sapa = new JLabel("Masuk sebagai " + namaAdmin + (admin ? " (admin)" : " (user)"), JLabel.CENTER);

        JPanel judul = new JPanel(new GridLayout(2, 1, 0, 4));
        judul.add(labelJudul("APLIKASI TIKET UNDIAN"));
        judul.add(sapa);

        JPanel ringkasan = new JPanel(new GridLayout(1, 4, 10, 0));
        ringkasan.add(kartu("Undian", nilaiUndian));
        ringkasan.add(kartu("Peserta", nilaiPeserta));
        ringkasan.add(kartu("Tiket Terjual", nilaiTiket));
        ringkasan.add(kartu("Pendapatan", nilaiPendapatan));

        JPanel menu = new JPanel(new GridLayout(2, 3, 12, 12));
        menu.setBorder(new EmptyBorder(16, 0, 0, 0));
        menu.add(tombolMenu("KELOLA UNDIAN", () -> buka(new FormUndian()), admin));
        menu.add(tombolMenu("KELOLA PESERTA", () -> buka(new FormPeserta()), true));
        menu.add(tombolMenu("JUAL TIKET", () -> buka(new FormTiket()), true));
        menu.add(tombolMenu("UNDI PEMENANG", () -> buka(new FormPengundian()), admin));
        menu.add(tombolMenu("LAPORAN PDF", () -> buka(new FormLaporan()), admin));
        menu.add(tombolMenu("LOGOUT", this::logout, true));

        JPanel isi = new JPanel(new BorderLayout(0, 16));
        isi.setBorder(new EmptyBorder(18, 24, 24, 24));
        isi.add(judul, BorderLayout.NORTH);
        JPanel tengah = new JPanel(new BorderLayout());
        tengah.add(ringkasan, BorderLayout.NORTH);
        tengah.add(menu, BorderLayout.CENTER);
        isi.add(tengah, BorderLayout.CENTER);
        setContentPane(isi);

        // Angka ringkasan dimuat saat menu tampil dan diperbarui setiap kali menu kembali aktif,
        // misalnya setelah menjual tiket.
        javax.swing.SwingUtilities.invokeLater(() -> jalankan(this::muat));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                jalankan(FormMenu.this::muat);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    SpringKonteks.tutup();
                } catch (RuntimeException ignored) {
                    // Aplikasi tetap ditutup walaupun konteks Spring gagal ditutup rapi.
                }
            }
        });
    }

    private void muat() {
        LayananUndian.Ringkasan r = layanan(LayananUndian.class).ringkasan();
        nilaiUndian.setText(String.valueOf(r.undian()));
        nilaiPeserta.setText(String.valueOf(r.peserta()));
        nilaiTiket.setText(String.valueOf(r.tiket()));
        nilaiPendapatan.setText(Format.rupiah(r.pendapatan()));
    }

    private void buka(FormDasar form) {
        form.setVisible(true);
    }

    private void logout() {
        for (Window w : Window.getWindows()) {
            if (w != this && w instanceof FormDasar) {
                w.dispose();
            }
        }
        dispose();
        new NewJFrame().setVisible(true);
    }

    private JButton tombolMenu(String teks, Runnable aksi, boolean aktif) {
        JButton tombol = new JButton(teks);
        tombol.setEnabled(aktif);
        if (!aktif) {
            tombol.setToolTipText("Hanya untuk admin");
        }
        tombol.setFont(tombol.getFont().deriveFont(Font.BOLD, 13f));
        tombol.addActionListener(e -> aksi.run());
        return tombol;
    }

    private static JLabel kartuNilai() {
        JLabel label = new JLabel("-", JLabel.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 18f));
        return label;
    }

    private static JPanel kartu(String judul, JLabel nilai) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 4));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(java.awt.Color.GRAY), new EmptyBorder(10, 6, 10, 6)));
        panel.add(new JLabel(judul, JLabel.CENTER));
        panel.add(nilai);
        return panel;
    }

    /** Dipakai untuk menjalankan menu ini sendiri lewat Run File. */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FormMenu().setVisible(true));
    }
}
