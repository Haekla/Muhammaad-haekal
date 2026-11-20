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

@Repository
@Transactional
public class AdminDao {

    @PersistenceContext
    private EntityManager em;

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

    @Transactional(readOnly = true)
    public List<Admin> semua() {
        return em.createQuery("select a from Admin a order by a.id", Admin.class).getResultList();
    }

    public Admin tambah(String username, String password) {
        Admin admin = new Admin(username, sha256(password));
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
