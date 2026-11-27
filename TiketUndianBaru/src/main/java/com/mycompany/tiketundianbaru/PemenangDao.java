package com.mycompany.tiketundianbaru;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PemenangDao extends DaoDasar<Pemenang> {

    public PemenangDao() {
        super(Pemenang.class);
    }

    @Transactional(readOnly = true)
    public boolean sudahAda(Integer undianId) {
        Long hasil = em.createQuery("select count(p) from Pemenang p where p.undian.id = :id", Long.class)
                .setParameter("id", undianId)
                .getSingleResult();
        return hasil != null && hasil > 0;
    }
}