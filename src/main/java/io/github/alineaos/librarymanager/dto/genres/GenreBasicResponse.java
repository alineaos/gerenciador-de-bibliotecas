package io.github.alineaos.librarymanager.dto.genres;

import io.swagger.v3.oas.annotations.media.Schema;

public record GenreBasicResponse(
        @Schema(description = "The genre's unique id.", example = "1")
        Long id,

        @Schema(description = "The genre's name.", example = "Romance")
        String name
) {}
