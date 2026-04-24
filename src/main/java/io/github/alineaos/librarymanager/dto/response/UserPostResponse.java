package io.github.alineaos.librarymanager.dto.response;

import io.github.alineaos.librarymanager.domain.enums.UserRole;

import java.time.LocalDateTime;

public record UserPostResponse (
        Long id,
        String fullName,
        String email,
        UserRole role,
        LocalDateTime createdAt
){}
