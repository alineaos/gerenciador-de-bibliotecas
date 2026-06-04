package io.github.alineaos.librarymanager.dto.books;

import io.swagger.v3.oas.annotations.media.Schema;

public record BookBasicResponse(
        @Schema(description = "The book's unique id.", example = "1")
        Long id,

        @Schema(description = "The book's title.", example = "Capitães da Areia")
        String title,

        @Schema(description = "The book's author name.", example = "Jorge Amado")
        String author
) {}
