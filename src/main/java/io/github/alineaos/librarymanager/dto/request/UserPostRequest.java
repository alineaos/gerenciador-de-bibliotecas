package io.github.alineaos.librarymanager.dto.request;

import io.github.alineaos.librarymanager.domain.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record UserPostRequest(
        @NotBlank(message = "O nome completo não pode estar em branco.")
        String fullName,

        @NotBlank(message = "O e-mail não pode estar em branco.")
        @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,10}$", message = "o e-mail não é válido.")
        String email,

        @NotBlank(message = "O cpf não pode estar em branco.")
        @Pattern(regexp = "^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$", message = "O CPF não é válido.")
        String cpf,

        @NotNull(message = "A data de nascimento não pode estar vazio.")
        @Past(message = "A data de nascimento deve estar no passado.")
        LocalDate birthDate,

        @NotNull(message = "O cargo não pode estar vazio.")
        UserRole role,

        @NotBlank(message = "A senha não pode estar em branco.")
        String password
) {}
