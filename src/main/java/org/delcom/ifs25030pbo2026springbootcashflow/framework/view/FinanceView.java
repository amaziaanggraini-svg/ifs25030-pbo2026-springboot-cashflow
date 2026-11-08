package org.delcom.ifs25030pbo2026springbootcashflow.framework.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.delcom.ifs25030pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25030pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25030pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25030pbo2026springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25030pbo2026springbootcashflow.domain.exception.DomainException;
import org.delcom.ifs25030pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25030pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25030pbo2026springbootcashflow.usecase.FinanceUseCase;

import java.util.Comparator;
import java.util.List;

/**
 * Tampilan web Vaadin aplikasi Cashflow.
 * Exception domain ditangani di sini (try-catch) lalu ditampilkan lewat Notification.
 */
@Route("")
@PageTitle("Cashflow")
public class FinanceView extends VerticalLayout {

    private final FinanceUseCase useCase;
    private final FinancePresenter presenter;

    private final Grid<Transaction> grid = new Grid<>(Transaction.class, false);
    private final TextField searchField = new TextField();
    private final Select<SortOption> sortSelect = new Select<>();
    private final Span emptyState = new Span("- Data transaksi belum tersedia!");
    private final Span totalCount = new Span("0");
    private final Span incomeTotal = new Span("Rp 0.00");
    private final Span expenseTotal = new Span("Rp 0.00");
    private final Span balanceLabel = new Span("Rp 0.00");

    public FinanceView(FinanceUseCase useCase, FinancePresenter presenter) {
        this.useCase = useCase;
        this.presenter = presenter;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        configureGrid();
        configureToolbar();

        add(new H1("Cashflow"), buildSummaryCards(), buildToolbar(), grid);
        refreshAll();
    }

    // ------------------------------------------------------------ ringkasan
    private HorizontalLayout buildSummaryCards() {
        HorizontalLayout summary = new HorizontalLayout(
                createSummaryCard("Total Transaksi", totalCount, "#DBEAFE", "#1D4ED8", "#1E40AF"),
                createSummaryCard("Total Pemasukan", incomeTotal, "#DCFCE7", "#16A34A", "#166534"),
                createSummaryCard("Total Pengeluaran", expenseTotal, "#FEE2E2", "#DC2626", "#991B1B"),
                createSummaryCard("Saldo", balanceLabel, "#FEF3C7", "#D97706", "#92400E")
        );
        summary.setWidthFull();
        summary.setSpacing(true);
        summary.getStyle().set("align-items", "stretch");
        return summary;
    }

    private Div createSummaryCard(String label, Span value, String background, String accent, String valueColor) {
        Span caption = new Span(label);
        caption.getStyle()
                .set("display", "block")
                .set("font-size", "0.875rem")
                .set("font-weight", "500")
                .set("color", accent);

        value.getStyle()
                .set("display", "block")
                .set("margin-top", "0.35rem")
                .set("font-size", "1.5rem")
                .set("font-weight", "700")
                .set("color", valueColor);

        Div card = new Div(caption, value);
        card.getStyle()
                .set("flex", "1")
                .set("min-width", "0")
                .set("background", background)
                .set("border", "1px solid " + accent)
                .set("border-left", "6px solid " + accent)
                .set("border-radius", "12px")
                .set("padding", "1rem 1.1rem")
                .set("box-sizing", "border-box");
        return card;
    }

    private void updateSummary() {
        totalCount.setText(String.valueOf(useCase.getAllTransactions().size()));
        incomeTotal.setText(presenter.formatMoney(useCase.getTotalIncome()));
        expenseTotal.setText(presenter.formatMoney(useCase.getTotalExpense()));
        balanceLabel.setText(presenter.formatMoney(useCase.getBalance()));
    }

    // ----------------------------------------------------------------- grid
    private void configureGrid() {
        grid.addColumn(Transaction::getId)
                .setHeader("ID").setKey("id")
                .setAutoWidth(true).setFlexGrow(0)
                .setSortable(true).setComparator(Transaction::getId);

        grid.addColumn(Transaction::getDescription)
                .setHeader("Keterangan").setKey("description")
                .setFlexGrow(1)
                .setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getDescription, String.CASE_INSENSITIVE_ORDER));

        grid.addColumn(t -> presenter.formatMoney(t.getAmount()))
                .setHeader("Jumlah").setKey("amount")
                .setAutoWidth(true)
                .setSortable(true)
                .setComparator(SortOption.AMOUNT_ASC.comparator());

        grid.addColumn(t -> presenter.formatType(t.getType()))
                .setHeader("Tipe").setKey("type")
                .setAutoWidth(true)
                .setSortable(true)
                .setComparator(SortOption.INCOME_FIRST.comparator());

        grid.addColumn(presenter::formatCreatedAt)
                .setHeader("Waktu").setKey("createdAt")
                .setAutoWidth(true)
                .setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getCreatedAt));

        grid.addComponentColumn(this::createActionButtons)
                .setHeader("Tindakan").setKey("tindakan")
                .setAutoWidth(true).setFlexGrow(0)
                .setSortable(false);

        grid.setEmptyStateComponent(emptyState);
        grid.setSizeFull();
    }

    private HorizontalLayout createActionButtons(Transaction transaction) {
        Button deleteButton = new Button("Hapus", VaadinIcon.TRASH.create(),
                event -> openDeleteDialog(transaction));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY_INLINE);
        return new HorizontalLayout(deleteButton);
    }

    // -------------------------------------------------------------- toolbar
    private void configureToolbar() {
        searchField.setPlaceholder("Cari keterangan...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);

        sortSelect.setPlaceholder("Urutkan...");
        sortSelect.setItems(SortOption.values());
        sortSelect.setItemLabelGenerator(presenter::formatSortOption);
        sortSelect.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                sortTransactions(event.getValue());
            }
        });
    }

    private HorizontalLayout buildToolbar() {
        Button searchButton = new Button("Cari", VaadinIcon.SEARCH.create(), event -> searchTransactions());
        Button refreshButton = new Button("Muat Ulang", VaadinIcon.REFRESH.create(), event -> refreshAll());

        Button incomeButton = new Button("Tambah Pemasukan", VaadinIcon.PLUS.create(),
                event -> openAddDialog(TransactionType.INCOME));
        incomeButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);

        Button expenseButton = new Button("Tambah Pengeluaran", VaadinIcon.MINUS.create(),
                event -> openAddDialog(TransactionType.EXPENSE));
        expenseButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout toolbar = new HorizontalLayout(
                searchField, searchButton, sortSelect, refreshButton, incomeButton, expenseButton);
        toolbar.setWidthFull();
        toolbar.setFlexGrow(1, searchField);
        return toolbar;
    }

    // --------------------------------------------------------------- aksi
    private void refreshAll() {
        searchField.clear();
        sortSelect.clear();
        showTransactions(useCase.getAllTransactions());
    }

    private void searchTransactions() {
        sortSelect.clear();
        showTransactions(useCase.searchTransactions(searchField.getValue()));
    }

    private void sortTransactions(SortOption option) {
        searchField.clear();
        showTransactions(useCase.sortTransactions(option));
    }

    private void showTransactions(List<Transaction> list) {
        grid.setItems(list);
        updateSummary();
    }

    private void openAddDialog(TransactionType type) {
        boolean income = type == TransactionType.INCOME;

        TextField descriptionField = new TextField("Keterangan");
        descriptionField.setWidthFull();

        NumberField amountField = new NumberField("Jumlah (Rp)");
        amountField.setWidthFull();
        amountField.setMin(0);
        amountField.setStep(1000);

        Dialog dialog = new Dialog();
        Button saveButton = new Button("Simpan", VaadinIcon.CHECK.create(), event -> {
            try {
                Transaction created = income
                        ? useCase.addIncome(descriptionField.getValue(), amountField.getValue())
                        : useCase.addExpense(descriptionField.getValue(), amountField.getValue());
                showSuccess(presenter.showAddSuccess(created));
                dialog.close();
                refreshAll();
            } catch (ValidationException ex) {
                showError(presenter.showValidationError(ex.getMessage()));
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Batal", VaadinIcon.CLOSE.create(), event -> dialog.close());

        dialog.setHeaderTitle(income ? "Tambah Pemasukan" : "Tambah Pengeluaran");
        dialog.add(new VerticalLayout(descriptionField, amountField));
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void openDeleteDialog(Transaction transaction) {
        Paragraph message = new Paragraph("Yakin ingin menghapus transaksi \""
                + transaction.getDescription() + "\" (ID " + transaction.getId() + ")?");
        message.getStyle().set("margin", "0");

        Dialog dialog = new Dialog();
        Button confirmButton = new Button("Hapus", VaadinIcon.TRASH.create(), event -> {
            deleteTransaction(transaction);
            dialog.close();
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button cancelButton = new Button("Batal", VaadinIcon.CLOSE.create(), event -> dialog.close());

        dialog.setHeaderTitle("Konfirmasi Hapus");
        dialog.add(message);
        dialog.getFooter().add(cancelButton, confirmButton);
        dialog.open();
    }

    private void deleteTransaction(Transaction transaction) {
        try {
            useCase.removeTransaction(transaction.getId());
            showSuccess(presenter.showRemoveSuccess());
            refreshAll();
        } catch (EntityNotFoundException ex) {
            showError(presenter.showRemoveFailed(ex.getId()));
        } catch (ValidationException ex) {
            showError(presenter.showValidationError(ex.getMessage()));
        } 
    }

    // ------------------------------------------------------------ notifikasi
    private void showSuccess(String message) {
        Notification n = Notification.show(message, 3000, Notification.Position.BOTTOM_START);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String message) {
        Notification n = Notification.show(message, 4000, Notification.Position.BOTTOM_START);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}