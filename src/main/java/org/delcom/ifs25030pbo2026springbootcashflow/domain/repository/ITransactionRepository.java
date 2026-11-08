package org.delcom.ifs25030pbo2026springbootcashflow.domain.repository;

import org.delcom.ifs25030pbo2026springbootcashflow.domain.entity.Transaction;

/**
 * Port spesifik Transaction. Menambah method penyimpanan khusus
 * pemasukan dan pengeluaran dengan penomoran ID otomatis.
 */
public interface ITransactionRepository extends IRepository<Transaction, Integer> {

    /** Menyimpan pemasukan baru. */
    Transaction saveIncome(String description, double amount);

    /** Menyimpan pengeluaran baru. */
    Transaction saveExpense(String description, double amount);
}