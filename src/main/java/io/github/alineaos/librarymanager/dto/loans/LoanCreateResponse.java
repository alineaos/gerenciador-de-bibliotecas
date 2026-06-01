package io.github.alineaos.librarymanager.dto.loans;

import io.github.alineaos.librarymanager.domain.enums.LoanStatus;
import io.github.alineaos.librarymanager.dto.books.BookBasicResponse;
import io.github.alineaos.librarymanager.dto.users.UserBasicResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoanCreateResponse(
        Long id,
        UserBasicResponse user,
        BookBasicResponse book,
        LoanStatus status,
        LocalDate borrowedAt,
        LocalDate dueAt,
        LocalDateTime createdAt
) {}
