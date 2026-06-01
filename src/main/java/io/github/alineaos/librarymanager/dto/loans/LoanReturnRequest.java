package io.github.alineaos.librarymanager.dto.loans;

import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record LoanReturnRequest(
        @PastOrPresent(message = "The field 'returnedAt' must be today or a date in the past.")
        LocalDate returnedAt
) {}
