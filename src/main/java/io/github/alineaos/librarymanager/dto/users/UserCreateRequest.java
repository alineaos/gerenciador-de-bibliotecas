package io.github.alineaos.librarymanager.dto.users;

import io.github.alineaos.librarymanager.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserCreateRequest(
        @NotBlank(message = "The field 'fullName' is required.")
        @Schema(description = "The user's full name.", example = "Maria Silva")
        String fullName,

        @NotBlank(message = "The field 'email' is required.")
        @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,10}$", message = "The e-mail is not valid.")
        @Schema(description = "The user's e-mail. Must be unique.", example = "maria.silva@testes.com")
        String email,

        @NotBlank(message = "The field 'cpf' is required.")
        @Pattern(regexp = "^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$", message = "The CPF is not valid.")
        @Schema(description = "The user's CPF. Must be unique.", example = "123.456.789.01")
        String cpf,

        @NotNull(message = "The field 'birthDate' is required.")
        @Past(message = "The birth date must be in the past.")
        @Schema(description = "The user's birth date. Must be a date in the past.", example = "1989-02-08")
        LocalDate birthDate,

        @NotNull(message = "The field 'role' is required.")
        @Schema(description = "The user's role.", example = "ADMIN")
        UserRole role,

        @NotBlank(message = "The field 'password' is required.")
        @Size(min = 8, message = "The password must have a minimum of 8 characters.")
        @Schema(description = "The user's password. Must have a minimum of 8 characters.", example = "Password@123")
        String password
) {}
