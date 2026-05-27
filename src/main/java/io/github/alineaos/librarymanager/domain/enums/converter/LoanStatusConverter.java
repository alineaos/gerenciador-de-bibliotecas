package io.github.alineaos.librarymanager.domain.enums.converter;

import io.github.alineaos.librarymanager.domain.enums.LoanStatus;
import jakarta.persistence.AttributeConverter;

public class LoanStatusConverter implements AttributeConverter<LoanStatus, String> {
    @Override
    public String convertToDatabaseColumn(LoanStatus attribute) {
        return attribute.getStatus();
    }

    @Override
    public LoanStatus convertToEntityAttribute(String dbData) {
        return LoanStatus.valueOf(dbData.toUpperCase());
    }
}
