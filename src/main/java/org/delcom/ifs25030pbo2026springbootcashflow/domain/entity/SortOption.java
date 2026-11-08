package org.delcom.ifs25030pbo2026springbootcashflow.domain.entity;

import java.util.Comparator;

/**
 * Kriteria pengurutan transaksi.
 * Setiap opsi membawa comparator-nya sendiri sehingga logika pengurutan
 * terpusat di domain, bukan tersebar sebagai angka ajaib di view / use case.
 */
public enum SortOption {
    /** Jumlah terkecil lebih dulu. */
    AMOUNT_ASC(Comparator.comparingDouble(Transaction::getAmount)),

    /** Jumlah terbesar lebih dulu. */
    AMOUNT_DESC(Comparator.comparingDouble(Transaction::getAmount).reversed()),

    /** Pemasukan ditampilkan lebih dulu (INCOME sebelum EXPENSE). */
    INCOME_FIRST(Comparator.comparing(Transaction::getType)),

    /** Pengeluaran ditampilkan lebih dulu (EXPENSE sebelum INCOME). */
    EXPENSE_FIRST(Comparator.comparing(Transaction::getType).reversed());

    private final Comparator<Transaction> comparator;

    SortOption(Comparator<Transaction> comparator) {
        this.comparator = comparator;
    }

    /** Comparator yang sesuai dengan opsi ini. */
    public Comparator<Transaction> comparator() {
        return comparator;
    }
}