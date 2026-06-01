package io.github.alineaos.librarymanager.dto.loans;

import io.github.alineaos.librarymanager.domain.enums.LoanStatus;

import java.time.LocalDate;

public record LoanFilter(
        Long userId,
        Long bookId,
        LoanStatus status,
        Boolean renewed,
        LocalDate borrowedAt,
        LocalDate dueAt,
        LocalDate returnedAt
) {}
