package io.github.alineaos.librarymanager.dto.request;

import io.github.alineaos.librarymanager.domain.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UserPatchRequest(
        String fullName,

        @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,10}$", message = "The e-mail is not valid.")
        String email,

        @Past(message = "The birth date must be in the past.")
        LocalDate birthDate,

        UserRole role,

        String password
) {}
