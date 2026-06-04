package io.github.alineaos.librarymanager.dto.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(
        @NotBlank(message = "The field 'email' is required.")
        @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,10}$", message = "The e-mail is not valid.")
        @Schema(description = "The user's e-mail.", example = "admin@library.com")
        String email,

        @NotBlank(message = "The field 'password' is required.")
        @Schema(description = "The user's password.", example = "Admin@123")
        String password
) {}
