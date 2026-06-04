package io.github.alineaos.librarymanager.dto.loans;

import io.github.alineaos.librarymanager.domain.enums.LoanStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record LoanFilter(
        @Schema(description = "Filter loans by their associated user id.", example = "1")
        Long userId,

        @Schema(description = "Filter loans by their associated book id.", example = "1")
        Long bookId,

        @Schema(description = "Filter loans by their current status.", example = "OVERDUE")
        LoanStatus status,

        @Schema(description = "Filter loans by their renewal status.", example = "true")
        Boolean renewed,

        @Schema(description = "Filter loans by their start date.", example = "2026-05-31")
        LocalDate borrowedAt,

        @Schema(description = "Filter loans by their due date.", example = "2026-06-14")
        LocalDate dueAt,

        @Schema(description = "Filter loans by their return date.", example = "2026-06-10")
        LocalDate returnedAt
) {}
