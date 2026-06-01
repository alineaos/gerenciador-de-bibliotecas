package io.github.alineaos.librarymanager.dto.users;

import io.github.alineaos.librarymanager.domain.enums.UserRole;

import java.time.LocalDateTime;

public record UserCreateResponse(
        Long id,
        String fullName,
        String email,
        UserRole role,
        LocalDateTime createdAt
){}
