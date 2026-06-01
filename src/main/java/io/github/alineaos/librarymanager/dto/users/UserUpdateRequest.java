package io.github.alineaos.librarymanager.dto.users;

import io.github.alineaos.librarymanager.domain.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserUpdateRequest(
        String fullName,

        @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,10}$", message = "The e-mail is not valid.")
        String email,

        @Past(message = "The birth date must be in the past.")
        LocalDate birthDate,

        UserRole role,

        @Size(min = 8, message = "The password must have a minimum of 8 characters.")
        String password
) {}
