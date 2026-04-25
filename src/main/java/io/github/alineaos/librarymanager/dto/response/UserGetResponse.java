package io.github.alineaos.librarymanager.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.alineaos.librarymanager.domain.enums.UserRole;
import io.github.alineaos.librarymanager.config.serializer.CpfSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserGetResponse (
        Long id,
        String fullName,
        String email,
        @JsonSerialize(using = CpfSerializer.class)
        String cpf,
        LocalDate birthDate,
        UserRole role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){
}
