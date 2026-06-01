package io.github.alineaos.librarymanager.service;

import io.github.alineaos.librarymanager.config.UnitTestConfig;
import io.github.alineaos.librarymanager.domain.entity.Loan;
import io.github.alineaos.librarymanager.domain.enums.LoanStatus;
import io.github.alineaos.librarymanager.repository.LoanRepository;
import io.github.alineaos.librarymanager.util.factories.GenreFactory;
import io.github.alineaos.librarymanager.util.factories.LoanFactory;
import io.github.alineaos.librarymanager.util.factories.UserFactory;
import io.github.alineaos.librarymanager.util.factories.AuthFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class LoanScheduledServiceTest extends UnitTestConfig {
    @InjectMocks
    private LoanScheduledService service;
    @Mock
    private LoanRepository repository;

    private final UserFactory userFactory = new UserFactory();
    private final GenreFactory genreFactory = new GenreFactory();
    private final AuthFactory.BookFactory bookFactory = new AuthFactory.BookFactory(genreFactory);
    private final LoanFactory loanFactory = new LoanFactory(userFactory, bookFactory);

    private List<Loan> loanList;

    @BeforeEach
    void init() {
        loanList = loanFactory.newLoanList();
    }

    @Test
    @Order(1)
    @DisplayName("processOverdueLoans updates the loan status when overdue loans exists")
    void processOverdueLoans_UpdatesLoanStatus_WhenOverdueLoansExists() {
        List<Loan> overdueLoans = loanList;
        LocalDate today = LocalDate.now();
        LocalDate overdueDate = today.minusDays(30);

        overdueLoans.forEach(l -> l.setDueAt(overdueDate));

        BDDMockito.when(repository.findOverdueLoansWithRelationships(any(), any())).thenReturn(overdueLoans);

        service.processOverdueLoans();

    Assertions.assertThat(overdueLoans)
            .extracting(Loan::getStatus)
            .containsOnly(LoanStatus.OVERDUE);
    }

    @Test
    @Order(2)
    @DisplayName("processOverdueLoans does nothing when no overdue loans are found")
    void processOverdueLoans_DoesNothing_WhenNoOverdueLoansAreFound() {
        BDDMockito.when(repository.findOverdueLoansWithRelationships(any(), any())).thenReturn(List.of());

        service.processOverdueLoans();

        Assertions.assertThatNoException()
                .isThrownBy(() -> service.processOverdueLoans());
    }
}