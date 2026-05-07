package io.github.alineaos.librarymanager.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GenrePutRequest(
        @NotBlank(message = "The field 'name' is required.")
        String name
) {}
