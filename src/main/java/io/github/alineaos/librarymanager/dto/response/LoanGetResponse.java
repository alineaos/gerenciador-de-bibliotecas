package io.github.alineaos.librarymanager.dto.response;

import io.github.alineaos.librarymanager.domain.enums.LoanStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoanGetResponse(
        Long id,
        UserBasicResponse user,
        BookBasicResponse book,
        LoanStatus status,
        boolean renewed,
        LocalDate borrowedAt,
        LocalDate dueAt,
        LocalDate returnedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
