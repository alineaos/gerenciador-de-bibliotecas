package io.github.alineaos.librarymanager.dto.genres;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record GenreCreateRequest(
        @NotBlank(message = "The field 'name' is required.")
        @Schema(description = "The genre's name. Must be unique.", example = "Romance")
        String name
) {}
