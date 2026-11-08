package org.delcom.ifs25030pbo2026springbootcashflow;

import org.delcom.ifs25030pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25030pbo2026springbootcashflow.domain.repository.ITransactionRepository;
import org.delcom.ifs25030pbo2026springbootcashflow.usecase.FinanceUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class AppTest {

    @Autowired
    private ITransactionRepository repository;

    @Autowired
    private FinanceUseCase useCase;

    @Autowired
    private FinancePresenter presenter;

    @Test
    void contextLoadsAndBeansWired() {
        assertNotNull(repository);
        assertNotNull(useCase);
        assertNotNull(presenter);
    }

    @Test
    void mainDelegatesToSpringApplicationRun() throws Exception {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(eq(App.class), any(String[].class))).thenReturn(null);
            App.main(new String[]{});
            mocked.verify(() -> SpringApplication.run(eq(App.class), any(String[].class)));
        }
    }
}