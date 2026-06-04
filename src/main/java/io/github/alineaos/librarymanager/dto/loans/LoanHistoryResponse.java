package io.github.alineaos.librarymanager.dto.loans;

import io.github.alineaos.librarymanager.domain.enums.LoanStatus;
import io.github.alineaos.librarymanager.dto.books.BookBasicResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record LoanHistoryResponse(
        @Schema(description = "The loan's unique id.", example = "1")
        Long id,

        @Schema(description = "The loan's associated book.")
        BookBasicResponse book,

        @Schema(description = "The loan's current status.", example = "BORROWED")
        LoanStatus status,

        @Schema(description = "The loan's renewal status. Indicates if it was already renewed.", example = "false")
        boolean renewed,

        @Schema(description = "The loan's start date.", example = "2026-05-31")
        LocalDate borrowedAt,

        @Schema(description = "The loan's due date. Can be 14 or 28 days after the loan date.", example = "2026-06-14")
        LocalDate dueAt,

        @Schema(description = "The loan's return date. Can be null.", example = "2026-06-10")
        LocalDate returnedAt
) {}
