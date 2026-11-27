package com.mycompany.tiketundianbaru;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PesertaDao extends DaoDasar<Peserta> {

    public PesertaDao() {
        super(Peserta.class);
    }
}