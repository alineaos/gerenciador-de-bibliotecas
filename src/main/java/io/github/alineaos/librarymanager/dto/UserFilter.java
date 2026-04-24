package io.github.alineaos.librarymanager.dto;

import io.github.alineaos.librarymanager.domain.enums.UserRole;

public record UserFilter(
        String name,
        UserRole role
) {}
