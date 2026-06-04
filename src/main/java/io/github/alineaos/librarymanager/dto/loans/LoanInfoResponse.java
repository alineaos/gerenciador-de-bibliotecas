package io.github.alineaos.librarymanager.dto.loans;

import io.github.alineaos.librarymanager.domain.enums.LoanStatus;
import io.github.alineaos.librarymanager.dto.books.BookBasicResponse;
import io.github.alineaos.librarymanager.dto.users.UserBasicResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoanInfoResponse(
        @Schema(description = "The loan's unique id.", example = "1")
        Long id,

        @Schema(description = "The loan's associated user.")
        UserBasicResponse user,

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
        LocalDate returnedAt,

        @Schema(description = "The date and time when the loan was created.", example = "2026-04-24T18:00:33")
        LocalDateTime createdAt,

        @Schema(description = "The date and time when the loan was last updated.", example = "2026-04-24T18:00:33")
        LocalDateTime updatedAt
) {}
