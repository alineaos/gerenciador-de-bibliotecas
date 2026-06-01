package io.github.alineaos.librarymanager.dto.genres;

import jakarta.validation.constraints.NotBlank;

public record GenreUpdateRequest(
        @NotBlank(message = "The field 'name' is required.")
        String name
) {}
