package io.github.alineaos.librarymanager.dto.errors;

import java.time.LocalDateTime;

public record DefaultMessageError(
        int status,
        String message,
        LocalDateTime timestamp
) {}


