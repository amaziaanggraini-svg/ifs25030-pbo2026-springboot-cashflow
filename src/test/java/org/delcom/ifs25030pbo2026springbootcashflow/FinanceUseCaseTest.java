package org.delcom.ifs25030pbo2026springbootcashflow;

import org.delcom.ifs25030pbo2026springbootcashflow.adapter.repository.TransactionRepository;
import org.delcom.ifs25030pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25030pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25030pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25030pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25030pbo2026springbootcashflow.usecase.FinanceUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FinanceUseCaseTest {

    private FinanceUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FinanceUseCase(new TransactionRepository());
    }

    @Test
    void balanceFollowsIncomeAndExpense() throws Exception {
        useCase.addIncome("Gaji", 5_000_000.0);
        useCase.addExpense("Makan", 25_000.0);
        assertEquals(4_975_000.0, useCase.getBalance(), 0.001);
    }

    @Test
    void removeUpdatesBalance() throws Exception {
        Transaction t = useCase.addExpense("Kopi", 20_000.0);
        useCase.removeTransaction(t.getId());
        assertEquals(0.0, useCase.getBalance(), 0.001);
    }

    @Test
    void invalidInputRejected() {
        assertThrows(ValidationException.class, () -> useCase.addIncome("  ", 10.0));
        assertThrows(ValidationException.class, () -> useCase.addIncome("A", -5.0));
        assertThrows(ValidationException.class, () -> useCase.addIncome("A", 0.0));
        assertThrows(ValidationException.class, () -> useCase.addExpense("A", null));
        assertThrows(ValidationException.class, () -> useCase.addExpense("A", Double.NaN));
        assertThrows(ValidationException.class, () -> useCase.removeTransaction(0));
        assertThrows(EntityNotFoundException.class, () -> useCase.removeTransaction(99));
    }

    @Test
    void searchIsCaseInsensitive() throws Exception {
        useCase.addIncome("Gaji Bulanan", 1000.0);
        useCase.addExpense("Makan", 10.0);
        assertEquals(1, useCase.searchTransactions("gAjI").size());
    }

    @Test
    void sortOptionsWork() throws Exception {
        useCase.addExpense("B", 50.0);
        useCase.addIncome("A", 10.0);
        useCase.addIncome("C", 100.0);

        List<Transaction> asc = useCase.sortTransactions(SortOption.AMOUNT_ASC);
        assertEquals(10.0, asc.get(0).getAmount());
        List<Transaction> desc = useCase.sortTransactions(SortOption.AMOUNT_DESC);
        assertEquals(100.0, desc.get(0).getAmount());
        assertTrue(useCase.sortTransactions(SortOption.INCOME_FIRST).get(0).isIncome());
        assertTrue(useCase.sortTransactions(SortOption.EXPENSE_FIRST).get(0).isExpense());
    }

    @Test
    void findAllReturnsDefensiveCopy() throws Exception {
        useCase.addIncome("A", 1.0);
        useCase.getAllTransactions().clear();
        assertEquals(1, useCase.getAllTransactions().size());
    }
}