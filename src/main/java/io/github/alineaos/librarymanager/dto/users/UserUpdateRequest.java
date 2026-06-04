package io.github.alineaos.librarymanager.dto.users;

import io.github.alineaos.librarymanager.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserUpdateRequest(
        @Schema(description = "The user's full name.", example = "Maria Silva")
        String fullName,

        @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,10}$", message = "The e-mail is not valid.")
        @Schema(description = "The user's e-mail. Must be unique.", example = "maria.silva@testes.com")
        String email,

        @Past(message = "The birth date must be in the past.")
        @Schema(description = "The user's birth date. Must be a date in the past.", example = "1989-02-08")
        LocalDate birthDate,

        @Schema(description = "The user's role.", example = "ADMIN")
        UserRole role,

        @Size(min = 8, message = "The password must have a minimum of 8 characters.")
        @Schema(description = "The user's password. Must have a minimum of 8 characters.", example = "Password@123")
        String password
) {}
