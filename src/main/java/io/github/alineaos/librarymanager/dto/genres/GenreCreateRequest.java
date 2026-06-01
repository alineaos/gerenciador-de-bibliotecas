package io.github.alineaos.librarymanager.dto.genres;

import jakarta.validation.constraints.NotBlank;

public record GenreCreateRequest(
        @NotBlank(message = "The field 'name' is required.")
        String name
) {}
