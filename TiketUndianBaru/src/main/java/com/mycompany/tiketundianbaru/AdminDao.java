package com.mycompany.tiketundianbaru;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO untuk tabel admin dengan gaya Spring.
 * @Repository menandai kelas ini sebagai lapisan akses data, lalu Spring menerjemahkan galat database
 * menjadi DataAccessException. @Transactional membuat Spring membuka, menyimpan, atau membatalkan
 * transaksi sendiri, jadi kode di sini tidak perlu beginTransaction, commit, atau rollback.
 * EntityManager disuntikkan oleh Spring lewat @PersistenceContext.
 *
 * Password disimpan sebagai hash SHA-256 huruf kecil. Hasilnya sama dengan SHA2(x, 256) di MySQL,
 * jadi akun lama di tabel admin tetap bisa dipakai login.
 */
@Repository
@Transactional
public class AdminDao {

    @PersistenceContext
    private EntityManager em;

    /** Mengembalikan Admin jika username dan password cocok, selain itu null. */
    @Transactional(readOnly = true)
    public Admin login(String username, String password) {
        List<Admin> hasil = em
                .createQuery("select a from Admin a where a.username = :username and a.password = :password", Admin.class)
                .setParameter("username", username)
                .setParameter("password", sha256(password))
                .setMaxResults(1)
                .getResultList();
        return hasil.isEmpty() ? null : hasil.get(0);
    }

    @Transactional(readOnly = true)
    public boolean usernameSudahAda(String username) {
        Long jumlah = em
                .createQuery("select count(a) from Admin a where a.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return jumlah > 0;
    }

    /** Mengambil semua admin, dipakai untuk tabel di FormLaporan dan sebagai data laporan JasperReports. */
    @Transactional(readOnly = true)
    public List<Admin> semua() {
        return em.createQuery("select a from Admin a order by a.id", Admin.class).getResultList();
    }

    /** Menyimpan akun baru. Jika method ini melempar galat, Spring membatalkan transaksinya. */
    public Admin tambah(String username, String password) {
        return tambah(username, password, Admin.ROLE_ADMIN);
    }

    /** Menyimpan akun baru dengan peran ADMIN atau USER. */
    public Admin tambah(String username, String password, String role) {
        Admin admin = new Admin(username, sha256(password), role);
        em.persist(admin);
        return admin;
    }

    static String sha256(String teks) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(teks.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
