package com.mycompany.tiketundianbaru;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UndianDao extends DaoDasar<Undian> {

    public UndianDao() {
        super(Undian.class);
    }

    @Transactional(readOnly = true)
    public List<Undian> semuaBuka() {
        return em.createQuery("select u from Undian u where u.status = :status order by u.id", Undian.class)
                .setParameter("status", Undian.STATUS_BUKA)
                .getResultList();
    }
}