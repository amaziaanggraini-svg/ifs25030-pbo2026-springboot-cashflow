package org.delcom.ifs25030pbo2026springbootcashflow.domain.entity;

import java.time.LocalDateTime;

/**
 * Entity inti yang merepresentasikan satu transaksi keuangan.
 * Berada di layer domain: tidak bergantung pada UI, framework, maupun penyimpanan.
 */
public class Transaction {

    /** ID unik transaksi, tidak boleh diubah setelah dibuat. */
    private final int id;

    /** Keterangan transaksi. */
    private String description;

    /** Jumlah uang (selalu positif; arah ditentukan oleh {@link #type}). */
    private double amount;

    /** Jenis transaksi: pemasukan atau pengeluaran. */
    private final TransactionType type;

    /** Waktu transaksi dibuat. */
    private final LocalDateTime createdAt;

    public Transaction(int id, String description, double amount, TransactionType type) {
        this(id, description, amount, type, LocalDateTime.now());
    }

    public Transaction(int id, String description, double amount, TransactionType type, LocalDateTime createdAt) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }

    public boolean isExpense() {
        return type == TransactionType.EXPENSE;
    }

    /** Jumlah bertanda: positif untuk pemasukan, negatif untuk pengeluaran. */
    public double signedAmount() {
        return isIncome() ? amount : -amount;
    }

    /** Mengubah keterangan transaksi. */
    public void changeDescription(String description) {
        this.description = description;
    }

    /** Mengubah jumlah transaksi. */
    public void changeAmount(double amount) {
        this.amount = amount;
    }
}