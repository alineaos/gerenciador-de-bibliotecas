package io.github.alineaos.librarymanager.service;

import io.github.alineaos.librarymanager.domain.entity.Loan;
import io.github.alineaos.librarymanager.domain.enums.LoanStatus;
import io.github.alineaos.librarymanager.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanScheduledService {
    private final LoanRepository repository;

    @Scheduled(initialDelayString = "${scheduling.initial-delay}", fixedDelayString = "${scheduling.fixed-delay}")
    @Transactional
    public void processOverdueLoans(){
        log.info("Scheduled Task: Starting verification of overdue loans");

        Set<LoanStatus> activeStatus = LoanStatus.getActiveStatus().stream()
                .filter(status -> status != LoanStatus.OVERDUE)
                .collect(Collectors.toSet());

        LocalDate today = LocalDate.now();

        List<Loan> overdueLoans = repository.findOverdueLoansWithRelationships(activeStatus, today);

        log.info("Scheduled Task: Found {} loans that became overdue today.", overdueLoans.size());

        for (Loan overdueLoan : overdueLoans) {
            overdueLoan.setStatus(LoanStatus.OVERDUE);
        }
        log.info("Scheduled Task: Successfully processed and updated {} loans to OVERDUE status", overdueLoans.size());
    }


}
