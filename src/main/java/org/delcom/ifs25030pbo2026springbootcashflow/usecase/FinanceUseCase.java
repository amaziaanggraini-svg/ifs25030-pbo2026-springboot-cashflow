package org.delcom.ifs25030pbo2026springbootcashflow.usecase;

import org.delcom.ifs25030pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25030pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25030pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25030pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25030pbo2026springbootcashflow.domain.repository.ITransactionRepository;

import java.util.List;

/**
 * Use case logika bisnis Cashflow.
 * Tidak ada I/O UI (tanpa Vaadin / System.out); hanya memproses data dan
 * melempar checked domain exception bila operasi tidak valid.
 */
public class FinanceUseCase {

    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final ITransactionRepository repository;

    public FinanceUseCase(ITransactionRepository repository) {
        this.repository = repository;
    }

    /** Semua transaksi. */
    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    /** Menambah pemasukan setelah validasi. */
    public Transaction addIncome(String description, Double amount) throws ValidationException {
        validate(description, amount);
        return repository.saveIncome(description.trim(), amount);
    }

    /** Menambah pengeluaran setelah validasi. */
    public Transaction addExpense(String description, Double amount) throws ValidationException {
        validate(description, amount);
        return repository.saveExpense(description.trim(), amount);
    }

    /** Menghapus transaksi; melempar exception jika ID tidak valid / tidak ditemukan. */
    public void removeTransaction(int id) throws EntityNotFoundException, ValidationException {
        if (id <= 0) {
            throw new ValidationException("ID tidak valid");
        }
        if (!repository.deleteById(id)) {
            throw new EntityNotFoundException(id);
        }
    }

    /** Mencari transaksi berdasarkan keterangan (case-insensitive). */
    public List<Transaction> searchTransactions(String keyword) {
        String lower = keyword == null ? "" : keyword.trim().toLowerCase();
        return repository.findBy(t -> t.getDescription().toLowerCase().contains(lower));
    }

    /** Mengurutkan transaksi sesuai {@link SortOption}. */
    public List<Transaction> sortTransactions(SortOption option) {
        return repository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    /** Total seluruh pemasukan. */
    public double getTotalIncome() {
        return repository.findAll().stream()
                .filter(Transaction::isIncome)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /** Total seluruh pengeluaran. */
    public double getTotalExpense() {
        return repository.findAll().stream()
                .filter(Transaction::isExpense)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /** Saldo = total pemasukan - total pengeluaran. */
    public double getBalance() {
        return getTotalIncome() - getTotalExpense();
    }

    private void validate(String description, Double amount) throws ValidationException {
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Keterangan tidak boleh kosong");
        }
        if (amount == null || amount.isNaN() || amount.isInfinite() || amount <= 0) {
            throw new ValidationException("Jumlah harus berupa angka lebih dari 0");
        }
    }
}