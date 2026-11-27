package com.mycompany.tiketundianbaru;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class TiketDao extends DaoDasar<Tiket> {

    public TiketDao() {
        super(Tiket.class);
    }

    @Transactional(readOnly = true)
    public List<Tiket> perUndian(Integer undianId) {
        return em.createQuery("select t from Tiket t where t.undian.id = :id order by t.id", Tiket.class)
                .setParameter("id", undianId)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public long jumlahPerUndian(Integer undianId) {
        Long hasil = em.createQuery("select count(t) from Tiket t where t.undian.id = :id", Long.class)
                .setParameter("id", undianId)
                .getSingleResult();
        return hasil == null ? 0 : hasil;
    }

    @Transactional(readOnly = true)
    public long jumlahPerPeserta(Integer pesertaId) {
        Long hasil = em.createQuery("select count(t) from Tiket t where t.peserta.id = :id", Long.class)
                .setParameter("id", pesertaId)
                .getSingleResult();
        return hasil == null ? 0 : hasil;
    }

    @Transactional(readOnly = true)
    public long totalPendapatan() {
        List<Long> hasil = em.createQuery("select sum(t.harga) from Tiket t", Long.class).getResultList();
        return hasil.isEmpty() || hasil.get(0) == null ? 0 : hasil.get(0);
    }
}