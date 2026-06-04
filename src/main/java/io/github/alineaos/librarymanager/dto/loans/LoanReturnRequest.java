package io.github.alineaos.librarymanager.dto.loans;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record LoanReturnRequest(
        @PastOrPresent(message = "The field 'returnedAt' must be today or a date in the past.")
        @Schema(description = "The loan's return date. Can be null, the current date or a past date.", example = "2026-06-10")
        LocalDate returnedAt
) {}
