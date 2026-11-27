package com.mycompany.tiketundianbaru;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class FormDaftar extends JFrame {

    private final JTextField kolomUsername = new JTextField(20);
    private final JPasswordField kolomPassword = new JPasswordField(20);
    private final JPasswordField kolomKonfirmasi = new JPasswordField(20);

    public FormDaftar() {
        super("Tambah Admin");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel isi = new JPanel(new GridBagLayout());
        isi.setBorder(new EmptyBorder(24, 32, 24, 32));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;

        c.gridy = 0;
        c.insets = new Insets(0, 0, 18, 0);
        JLabel judul = new JLabel("TAMBAH ADMIN", JLabel.CENTER);
        judul.setFont(judul.getFont().deriveFont(java.awt.Font.BOLD, 16f));
        isi.add(judul, c);

        tambahBaris(isi, c, 1, "USERNAME", kolomUsername);
        tambahBaris(isi, c, 2, "PASSWORD", kolomPassword);
        tambahBaris(isi, c, 3, "ULANGI PASSWORD", kolomKonfirmasi);

        JButton tombolSimpan = new JButton("SIMPAN");
        JButton tombolBatal = new JButton("BATAL");
        tombolSimpan.addActionListener(e -> simpan());
        tombolBatal.addActionListener(e -> dispose());
        kolomKonfirmasi.addActionListener(e -> simpan());

        JPanel tombol = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));
        tombol.add(tombolSimpan);
        tombol.add(tombolBatal);
        c.gridy = 4;
        c.insets = new Insets(10, 0, 0, 0);
        isi.add(tombol, c);

        setContentPane(isi);
        getRootPane().setDefaultButton(tombolSimpan);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void tambahBaris(JPanel isi, GridBagConstraints c, int baris, String label, JTextField kolom) {
        GridBagConstraints kiri = (GridBagConstraints) c.clone();
        kiri.gridy = baris;
        kiri.gridwidth = 1;
        kiri.weightx = 0;
        kiri.insets = new Insets(0, 0, 10, 14);
        isi.add(new JLabel(label), kiri);

        GridBagConstraints kanan = (GridBagConstraints) c.clone();
        kanan.gridy = baris;
        kanan.gridx = 1;
        kanan.gridwidth = 1;
        kanan.weightx = 1;
        kanan.insets = new Insets(0, 0, 10, 0);
        isi.add(kolom, kanan);
    }

    static String periksa(String username, String password, String konfirmasi) {
        if (username.isEmpty() || password.isEmpty() || konfirmasi.isEmpty()) {
            return "Semua kolom wajib diisi!";
        }
        if (username.contains(" ")) {
            return "Username tidak boleh memakai spasi.";
        }
        if (username.length() > 50) {
            return "Username maksimal 50 karakter.";
        }
        if (password.length() < 4) {
            return "Password minimal 4 karakter.";
        }
        if (!password.equals(konfirmasi)) {
            return "Password dan ulangi password tidak sama.";
        }
        return null;
    }

    private void simpan() {
        String username = kolomUsername.getText().trim();
        String password = new String(kolomPassword.getPassword());
        String konfirmasi = new String(kolomKonfirmasi.getPassword());

        String galat = periksa(username, password, konfirmasi);
        if (galat != null) {
            JOptionPane.showMessageDialog(this, galat, "Perhatian", JOptionPane.WARNING_MESSAGE);
            return;
        }

        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        try {
            AdminDao adminDao = SpringKonteks.ambil(AdminDao.class);
            if (adminDao.usernameSudahAda(username)) {
                JOptionPane.showMessageDialog(this, "Username sudah dipakai. Pilih username lain.",
                        "Perhatian", JOptionPane.WARNING_MESSAGE);
                return;
            }
            adminDao.tambah(username, password);
            JOptionPane.showMessageDialog(this, "Akun " + username + " berhasil disimpan.");
            kosongkan();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan ke database: " + SpringKonteks.pesanGalat(e),
                    "Galat", JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(java.awt.Cursor.getDefaultCursor());
        }
    }

    private void kosongkan() {
        kolomUsername.setText("");
        kolomPassword.setText("");
        kolomKonfirmasi.setText("");
        kolomUsername.requestFocusInWindow();
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            FormDaftar form = new FormDaftar();
            form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            form.setVisible(true);
        });
    }
}
