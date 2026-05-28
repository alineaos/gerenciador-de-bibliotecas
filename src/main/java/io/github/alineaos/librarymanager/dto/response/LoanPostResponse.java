package io.github.alineaos.librarymanager.dto.response;

import io.github.alineaos.librarymanager.domain.enums.LoanStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoanPostResponse(
        Long id,
        UserBasicResponse user,
        BookBasicResponse book,
        LoanStatus status,
        LocalDate borrowedAt,
        LocalDate dueAt,
        LocalDateTime createdAt
) {}
