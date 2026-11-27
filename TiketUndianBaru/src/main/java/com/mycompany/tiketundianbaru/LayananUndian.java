package com.mycompany.tiketundianbaru;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aturan bisnis aplikasi tiket undian: menjual tiket, mengundi pemenang, dan menyusun ringkasan.
 * Satu method di sini berjalan dalam satu transaksi. Jika ada galat di tengah jalan,
 * Spring membatalkan semua perubahan, jadi data tidak setengah tersimpan.
 * Layanan ini memakai DAO, dan DAO disuntikkan Spring lewat constructor.
 */
@Service
@Transactional
public class LayananUndian {

    public static final int MAKS_TIKET_SEKALI_JUAL = 50;

    /** Angka ringkasan untuk halaman menu utama. */
    public record Ringkasan(long undian, long peserta, long tiket, long pendapatan) {
    }

    private final UndianDao undianDao;
    private final PesertaDao pesertaDao;
    private final TiketDao tiketDao;
    private final PemenangDao pemenangDao;

    public LayananUndian(UndianDao undianDao, PesertaDao pesertaDao, TiketDao tiketDao, PemenangDao pemenangDao) {
        this.undianDao = undianDao;
        this.pesertaDao = pesertaDao;
        this.tiketDao = tiketDao;
        this.pemenangDao = pemenangDao;
    }

    /**
     * Menjual sejumlah tiket kepada satu peserta. Nomor tiket dibuat berurutan per undian,
     * misalnya U1-0001, U1-0002, dan seterusnya.
     */
    public List<Tiket> jualTiket(Integer undianId, Integer pesertaId, int jumlah) {
        if (jumlah < 1 || jumlah > MAKS_TIKET_SEKALI_JUAL) {
            throw new IllegalArgumentException("Jumlah tiket harus 1 sampai " + MAKS_TIKET_SEKALI_JUAL + ".");
        }
        Undian undian = undianDao.cari(undianId);
        if (undian == null) {
            throw new IllegalArgumentException("Undian tidak ditemukan.");
        }
        if (!undian.isBuka()) {
            throw new IllegalStateException("Undian " + undian.getNama() + " sudah ditutup.");
        }
        Peserta peserta = pesertaDao.cari(pesertaId);
        if (peserta == null) {
            throw new IllegalArgumentException("Peserta tidak ditemukan.");
        }

        long sudahTerjual = tiketDao.jumlahPerUndian(undianId);
        List<Tiket> hasil = new ArrayList<>();
        for (int i = 1; i <= jumlah; i++) {
            String nomor = String.format("U%d-%04d", undianId, sudahTerjual + i);
            hasil.add(tiketDao.simpan(new Tiket(nomor, undian, peserta, undian.getHargaTiket())));
        }
        return hasil;
    }

    /** Mengundi satu pemenang dari semua tiket undian ini, lalu menutup undian. */
    public Pemenang undi(Integer undianId) {
        return undi(undianId, new SecureRandom());
    }

    /** Versi dengan Random dari luar, dipakai pengujian agar hasilnya bisa diprediksi. */
    Pemenang undi(Integer undianId, Random acak) {
        Undian undian = undianDao.cari(undianId);
        if (undian == null) {
            throw new IllegalArgumentException("Undian tidak ditemukan.");
        }
        if (!undian.isBuka() || pemenangDao.sudahAda(undianId)) {
            throw new IllegalStateException("Undian " + undian.getNama() + " sudah diundi.");
        }

        Tiket menang = Pengundi.pilih(tiketDao.perUndian(undianId), acak);
        Pemenang pemenang = pemenangDao.simpan(new Pemenang(undian, menang, undian.getHadiah()));

        undian.setStatus(Undian.STATUS_SELESAI);
        undianDao.simpan(undian);
        return pemenang;
    }

    @Transactional(readOnly = true)
    public Ringkasan ringkasan() {
        return new Ringkasan(undianDao.jumlah(), pesertaDao.jumlah(), tiketDao.jumlah(), tiketDao.totalPendapatan());
    }
}
