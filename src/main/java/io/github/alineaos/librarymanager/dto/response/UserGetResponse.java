package io.github.alineaos.librarymanager.dto.response;

import io.github.alineaos.librarymanager.domain.enums.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserGetResponse (
        Long id,
        String fullName,
        String email,
        String cpf,
        LocalDate birthDate,
        UserRole role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){
}
