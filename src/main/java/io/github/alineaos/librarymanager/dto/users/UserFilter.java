package io.github.alineaos.librarymanager.dto.users;

import io.github.alineaos.librarymanager.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserFilter(
        @Schema(description = "Filter users by their name or part of it.", example = "Maria")
        String name,

        @Schema(description = "Filter users by their access role in the system.", example = "ADMIN")
        UserRole role
) {}
