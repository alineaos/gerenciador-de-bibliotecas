package io.github.alineaos.librarymanager.dto.loans;

import io.github.alineaos.librarymanager.domain.enums.LoanStatus;
import io.github.alineaos.librarymanager.dto.books.BookBasicResponse;

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
