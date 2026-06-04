package io.github.alineaos.librarymanager.dto.genres;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
public record GenreCreateResponse(
        @Schema(description = "The genre's unique id.", example = "1")
        Long id,

        @Schema(description = "The genre's name.", example = "Romance")
        String name,

        @Schema(description = "The date and time when the genre was created.", example = "2026-04-24T18:00:33")
        LocalDateTime createdAt
) {}
