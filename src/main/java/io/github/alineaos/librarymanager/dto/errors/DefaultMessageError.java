package io.github.alineaos.librarymanager.dto.errors;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record DefaultMessageError(
        @Schema(description = "The HTTP status code.", examples = {"400", "401", "403", "404"})
        int status,
        @Schema(description = "A description of the error.", example = "Invalid Credentials.")
        String message,
        @Schema(description = "The date and time when the error occurred.", example = "2026-06-03T16:43:16.3200304")
        LocalDateTime timestamp
) {}


