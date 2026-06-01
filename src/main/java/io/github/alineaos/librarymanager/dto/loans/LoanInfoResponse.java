package io.github.alineaos.librarymanager.dto.loans;

import io.github.alineaos.librarymanager.domain.enums.LoanStatus;
import io.github.alineaos.librarymanager.dto.books.BookBasicResponse;
import io.github.alineaos.librarymanager.dto.users.UserBasicResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoanInfoResponse(
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
