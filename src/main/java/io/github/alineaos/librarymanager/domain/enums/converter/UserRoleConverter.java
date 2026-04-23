package io.github.alineaos.librarymanager.domain.enums.converter;

import io.github.alineaos.librarymanager.domain.enums.UserRole;
import jakarta.persistence.AttributeConverter;

public class UserRoleConverter implements AttributeConverter<UserRole, String> {

    @Override
    public String convertToDatabaseColumn(UserRole attribute) {
        return attribute.getRole();
    }

    @Override
    public UserRole convertToEntityAttribute(String dbData) {
        return UserRole.valueOf(dbData);
    }
}
