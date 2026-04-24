package io.github.alineaos.librarymanager.dto.error;

import java.time.LocalDateTime;

public record DefaultMessageError(
        int status,
        String message,
        LocalDateTime timestamp
) {}


