package io.github.alineaos.librarymanager.dto.request;

import io.github.alineaos.librarymanager.domain.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UserPatchRequest(
        @NotBlank(message = "The field 'fullName' is required.")
        String fullName,

        @NotBlank(message = "The field 'email' is required.")
        @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,10}$", message = "The e-mail is not valid.")
        String email,

        @NotNull(message = "The field 'birthDate' is required.")
        @Past(message = "The birth date must be in the past.")
        LocalDate birthDate,

        @NotNull(message = "The field 'role' is required.")
        UserRole role,

        @NotBlank(message = "The field 'password' is required.")
        String password
) {}
