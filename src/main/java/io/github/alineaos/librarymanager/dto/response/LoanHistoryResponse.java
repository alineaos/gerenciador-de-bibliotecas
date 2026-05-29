package io.github.alineaos.librarymanager.dto.response;

import io.github.alineaos.librarymanager.domain.enums.LoanStatus;

import java.time.LocalDate;

public record LoanHistoryResponse(
        Long id,
        BookBasicResponse book,
        LoanStatus status,
        boolean renewed,
        LocalDate borrowedAt,
        LocalDate dueAt,
        LocalDate returnedAt
) {}
