package io.github.alineaos.librarymanager.domain.enums;

import lombok.Getter;

@Getter
public enum UserRole {

    ADMIN("Admin"),
    USER("User");

    private final String label;

    UserRole(String label) {
        this.label = label;
    }
}
