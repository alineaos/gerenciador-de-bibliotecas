package io.github.alineaos.librarymanager.dto.loans;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record LoanCreateRequest(
        @NotNull(message = "The field 'userId' is required.")
        @Positive(message = "The userId must be a positive number.")
        @Schema(description = "The loan's associated user id.", example = "1")
        Long userId,

        @NotNull(message = "The field 'bookId' is required.")
        @Positive(message = "The bookId must be a positive number.")
        @Schema(description = "The loan's associated book id.", example = "1")
        Long bookId,

        @PastOrPresent(message = "The field 'borrowedAt' must be today or a date in the past.")
        @Schema(description = "The loan's start date. Must be the current date or a past date", example = "2026-05-31")
        LocalDate borrowedAt
) {}
