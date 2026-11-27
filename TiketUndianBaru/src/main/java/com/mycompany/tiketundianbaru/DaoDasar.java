package com.mycompany.tiketundianbaru;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class DaoDasar<T extends Entitas> {

    @PersistenceContext
    protected EntityManager em;

    private final Class<T> kelas;

    protected DaoDasar(Class<T> kelas) {
        this.kelas = kelas;
    }

    @Transactional(readOnly = true)
    public List<T> semua() {
        return em.createQuery("select e from " + kelas.getSimpleName() + " e order by e.id", kelas)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public T cari(Integer id) {
        return id == null ? null : em.find(kelas, id);
    }

    @Transactional(readOnly = true)
    public long jumlah() {
        Long hasil = em.createQuery("select count(e) from " + kelas.getSimpleName() + " e", Long.class)
                .getSingleResult();
        return hasil == null ? 0 : hasil;
    }

    public T simpan(T objek) {
        if (objek.getId() == null) {
            em.persist(objek);
            return objek;
        }
        return em.merge(objek);
    }

    public void hapus(Integer id) {
        T objek = cari(id);
        if (objek != null) {
            em.remove(objek);
        }
    }
}