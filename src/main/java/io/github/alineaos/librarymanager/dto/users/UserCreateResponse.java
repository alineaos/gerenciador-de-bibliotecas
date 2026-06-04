package io.github.alineaos.librarymanager.dto.users;

import io.github.alineaos.librarymanager.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record UserCreateResponse(
        @Schema(description = "The user's unique id.", example = "1")
        Long id,

        @Schema(description = "The user's full name.", example = "Maria Silva")
        String fullName,

        @Schema(description = "The user's e-mail.", example = "maria.silva@testes.com")
        String email,

        @Schema(description = "The user's role.", example = "ADMIN")
        UserRole role,

        @Schema(description = "The date and time when the user was created.", example = "2026-04-24T18:00:33")
        LocalDateTime createdAt
) {}
