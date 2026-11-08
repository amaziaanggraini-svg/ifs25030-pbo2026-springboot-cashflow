package org.delcom.ifs25030pbo2026springbootcashflow.adapter.presenter;

import org.delcom.ifs25030pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25030pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25030pbo2026springbootcashflow.domain.entity.TransactionType;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Presenter: memformat data domain menjadi teks siap tampil.
 * Format tampilan bukan tanggung jawab entity / use case.
 */
public class FinancePresenter {

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Format uang: 1000000 -> "Rp 1,000,000.00". */
    public String formatMoney(double amount) {
        return String.format(Locale.US, "Rp %,.2f", amount);
    }

    /** Label jenis transaksi. */
    public String formatType(TransactionType type) {
        return type == TransactionType.INCOME ? "Pemasukan" : "Pengeluaran";
    }

    /** Waktu transaksi. */
    public String formatCreatedAt(Transaction transaction) {
        return transaction.getCreatedAt().format(DATE_TIME_FORMAT);
    }

    /** Satu baris transaksi: [id] keterangan | Rp x | [Tipe]. */
    public String format(Transaction t) {
        return String.format("[%d] %s | %s | [%s]",
                t.getId(), t.getDescription(), formatMoney(t.getAmount()), formatType(t.getType()));
    }

    /** Teks saldo. */
    public String formatBalance(double balance) {
        return "Saldo: " + formatMoney(balance);
    }

    /** Label opsi pengurutan untuk ditampilkan di UI. */
    public String formatSortOption(SortOption option) {
        return switch (option) {
            case AMOUNT_ASC -> "Jumlah terkecil";
            case AMOUNT_DESC -> "Jumlah terbesar";
            case INCOME_FIRST -> "Pemasukan dulu";
            case EXPENSE_FIRST -> "Pengeluaran dulu";
        };
    }

    private String formatList(List<Transaction> list, String header, String emptyMessage) {
        StringBuilder builder = new StringBuilder(header);
        if (list.isEmpty()) {
            return builder.append('\n').append(emptyMessage).toString();
        }
        String body = list.stream().map(this::format).collect(Collectors.joining("\n"));
        return builder.append('\n').append(body).toString();
    }

    public String showTransactions(List<Transaction> list) {
        return formatList(list, "Daftar Transaksi:", "- Data transaksi belum tersedia!");
    }

    public String showSearchResults(List<Transaction> list, String keyword) {
        return formatList(list, "Hasil Pencarian: \"" + keyword + "\"", "- Transaksi tidak ditemukan!");
    }

    public String showSortedTransactions(List<Transaction> list) {
        return formatList(list, "Daftar Transaksi (Terurut):", "- Data transaksi belum tersedia!");
    }

    public String showAddSuccess(Transaction t) {
        return "Berhasil menambah " + formatType(t.getType()).toLowerCase() + ": " + format(t);
    }

    public String showRemoveSuccess() {
        return "Berhasil menghapus transaksi.";
    }

    public String showRemoveFailed(int id) {
        return String.format("[!] Gagal menghapus transaksi dengan ID: %d.", id);
    }

    public String showInvalidId() {
        return "[!] ID tidak valid!";
    }

    public String showInvalidChoice() {
        return "[!] Pilihan tidak dimengerti.";
    }

    public String showValidationError(String message) {
        return "[!] " + message;
    }
}