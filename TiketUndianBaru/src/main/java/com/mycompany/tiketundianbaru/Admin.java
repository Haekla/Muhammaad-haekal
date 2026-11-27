package com.mycompany.tiketundianbaru;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entitas Hibernate untuk tabel admin di database db_tiketundian.
 * Satu objek Admin mewakili satu baris tabel. Kolom password menyimpan hash SHA-256, bukan teks asli.
 */
@Entity
@Table(name = "admin")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 64)
    private String password;

    /** Peran akun: ADMIN atau USER. Baris lama yang kolom role-nya kosong dianggap ADMIN. */
    @Column(name = "role", length = 10)
    private String role;

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";

    /** Dibutuhkan Hibernate untuk membuat objek dari baris tabel. */
    public Admin() {
    }

    public Admin(String username, String passwordHash) {
        this.username = username;
        this.password = passwordHash;
    }

    public Admin(String username, String passwordHash, String role) {
        this(username, passwordHash);
        this.role = role;
    }

    public String getRole() {
        return role == null || role.isBlank() ? ROLE_ADMIN : role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /** Benar jika akun ini admin. Akun USER hanya boleh mengelola peserta dan menjual tiket. */
    public boolean isAdmin() {
        return !ROLE_USER.equalsIgnoreCase(getRole());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Admin{id=" + id + ", username=" + username + ", role=" + getRole() + "}";
    }
}
