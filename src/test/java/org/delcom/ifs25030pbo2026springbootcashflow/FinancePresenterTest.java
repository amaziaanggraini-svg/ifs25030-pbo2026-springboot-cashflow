package org.delcom.ifs25030pbo2026springbootcashflow;

import org.delcom.ifs25030pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25030pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25030pbo2026springbootcashflow.domain.entity.TransactionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FinancePresenterTest {

    private final FinancePresenter presenter = new FinancePresenter();

    @Test
    void formatsMoneyWithThousandsAndTwoDecimals() {
        assertEquals("Rp 1,000,000.00", presenter.formatMoney(1_000_000));
    }

    @Test
    void formatsTransactionLineAndBalance() {
        Transaction t = new Transaction(1, "Gaji", 1_000_000, TransactionType.INCOME);
        assertEquals("[1] Gaji | Rp 1,000,000.00 | [Pemasukan]", presenter.format(t));
        assertEquals("Saldo: Rp 4,975,000.00", presenter.formatBalance(4_975_000));
    }
}