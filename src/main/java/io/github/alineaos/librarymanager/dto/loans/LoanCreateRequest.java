package io.github.alineaos.librarymanager.dto.loans;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record LoanCreateRequest(
        @NotNull(message = "The field 'userId' is required.")
        @Positive(message = "The userId must be a positive number.")
        Long userId,

        @NotNull(message = "The field 'bookId' is required.")
        @Positive(message = "The bookId must be a positive number.")
        Long bookId,

        @PastOrPresent(message = "The field 'borrowedAt' must be today or a date in the past.")
        LocalDate borrowedAt
) {}
