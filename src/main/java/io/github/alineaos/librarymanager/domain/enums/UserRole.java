package io.github.alineaos.librarymanager.domain.enums;

import lombok.Getter;

@Getter
public enum UserRole {

    ADMIN("Admin"),
    USER("User");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }
}
