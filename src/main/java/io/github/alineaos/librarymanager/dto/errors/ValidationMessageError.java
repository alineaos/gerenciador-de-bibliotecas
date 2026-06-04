package io.github.alineaos.librarymanager.dto.errors;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationMessageError(
        @Schema(description = "The HTTP status code.", example = "400")
        int status,
        @Schema(description = "A description of the error.", example = "Some fields could not be controller in service layer.")
        String message,
        @Schema(description = "The date and time when the error occurred.", example = "2026-06-03T16:43:16.3200304")
        LocalDateTime timestamp,
        @Schema(description = "The list of the specific field validation errors.")
        List<FieldError> errors
) {
public record FieldError(
        @Schema(description = "The name of the field that failed validation.", example = "password")
        String field,
        @Schema(description = "The validation error message explaining why it failed", example = "The password must have a minimum of 8 characters.")
        String message
) {}
}


