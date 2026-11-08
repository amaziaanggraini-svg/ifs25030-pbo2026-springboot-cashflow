package org.delcom.ifs25030pbo2026springbootcashflow.framework;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Meta;

/**
 * Konfigurasi app shell Vaadin: meta tag untuk SEO (meta description).
 */
@Meta(name = "description",
        content = "Cashflow: aplikasi web pencatat pemasukan dan pengeluaran dengan ringkasan saldo, "
                + "pencarian, dan pengurutan transaksi.")
public class AppShell implements AppShellConfigurator {
}
