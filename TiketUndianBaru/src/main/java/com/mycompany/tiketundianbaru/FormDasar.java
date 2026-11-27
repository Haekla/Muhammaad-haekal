package com.mycompany.tiketundianbaru;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

public abstract class FormDasar extends JFrame {

    protected interface Tugas {
        void kerjakan() throws Exception;
    }

    protected FormDasar(String judul, int lebar, int tinggi) {
        super(judul);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(lebar, tinggi);
        setLocationRelativeTo(null);
    }

    protected void info(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void peringatan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Perhatian", JOptionPane.WARNING_MESSAGE);
    }

    protected void galat(Throwable e) {
        JOptionPane.showMessageDialog(this, Galat.ramah(e), "Galat", JOptionPane.ERROR_MESSAGE);
    }

    protected boolean tanya(String pesan) {
        return JOptionPane.showConfirmDialog(this, pesan, "Konfirmasi", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    protected void jalankan(Tugas tugas) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            tugas.kerjakan();
        } catch (IllegalArgumentException | IllegalStateException e) {
            peringatan(e.getMessage());
        } catch (Exception e) {
            galat(e);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    protected static <T> T layanan(Class<T> kelas) {
        return SpringKonteks.ambil(kelas);
    }

    protected static DefaultTableModel modelBaca(String... kolom) {
        return new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int baris, int kolom) {
                return false;
            }
        };
    }

    protected static void rapikan(javax.swing.JTable tabel) {
        for (int i = 0; i < tabel.getColumnCount(); i++) {
            String nama = tabel.getColumnName(i);
            if ("No".equals(nama) || "ID".equals(nama)) {
                tabel.getColumnModel().getColumn(i).setMaxWidth(60);
                tabel.getColumnModel().getColumn(i).setPreferredWidth(50);
            }
        }
    }

    protected static JLabel labelJudul(String teks) {
        JLabel label = new JLabel(teks, JLabel.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        return label;
    }

    protected static void isiBaris(JPanel panel, int baris, String label, JComponent isian) {
        GridBagConstraints kiri = new GridBagConstraints();
        kiri.gridx = 0;
        kiri.gridy = baris;
        kiri.anchor = GridBagConstraints.WEST;
        kiri.insets = new Insets(4, 0, 4, 12);
        panel.add(new JLabel(label), kiri);

        GridBagConstraints kanan = new GridBagConstraints();
        kanan.gridx = 1;
        kanan.gridy = baris;
        kanan.weightx = 1;
        kanan.fill = GridBagConstraints.HORIZONTAL;
        kanan.insets = new Insets(4, 0, 4, 0);
        panel.add(isian, kanan);
    }
}
