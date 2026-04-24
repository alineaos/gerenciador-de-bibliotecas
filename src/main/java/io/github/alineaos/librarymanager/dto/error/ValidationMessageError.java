package io.github.alineaos.librarymanager.dto.error;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationMessageError(
        int status,
        String message,
        LocalDateTime timestamp,
        List<FieldError> errors
) {
public record FieldError(String field, String message) {}
}


