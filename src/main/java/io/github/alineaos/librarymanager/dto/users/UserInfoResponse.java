package io.github.alineaos.librarymanager.dto.users;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.alineaos.librarymanager.domain.enums.UserRole;
import io.github.alineaos.librarymanager.config.serializer.CpfSerializer;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserInfoResponse(
        @Schema(description = "The user's unique id.", example = "1")
        Long id,

        @Schema(description = "The user's full name.", example = "Maria Silva")
        String fullName,

        @Schema(description = "The user's e-mail.", example = "maria.silva@testes.com")
        String email,

        @JsonSerialize(using = CpfSerializer.class)
        @Schema(description = "The user's CPF.", example = "123.456.789.01")
        String cpf,

        @Schema(description = "The user's birth date.", example = "1989-02-08")
        LocalDate birthDate,

        @Schema(description = "The user's role.", example = "ADMIN")
        UserRole role,

        @Schema(description = "The date and time when the user was created.", example = "2026-04-24T18:00:33")
        LocalDateTime createdAt,

        @Schema(description = "The date and time when the user was last updated.", example = "2026-04-24T18:00:33")
        LocalDateTime updatedAt
){
}
