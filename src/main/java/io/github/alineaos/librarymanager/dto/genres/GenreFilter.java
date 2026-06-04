package io.github.alineaos.librarymanager.dto.genres;

import io.swagger.v3.oas.annotations.media.Schema;

public record GenreFilter(
        @Schema(description = "Filter genres by their name or part of it.", example = "Roma")
        String name
) {}
