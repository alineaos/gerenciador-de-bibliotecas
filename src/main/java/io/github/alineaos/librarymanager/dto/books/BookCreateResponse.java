package io.github.alineaos.librarymanager.dto.books;

import io.github.alineaos.librarymanager.dto.genres.GenreBasicResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record BookCreateResponse(
        @Schema(description = "The book's unique id.", example = "1")
        Long id,

        @Schema(description = "The book's title.", example = "Capitães da Areia")
        String title,

        @Schema(description = "The book's author name.", example = "Jorge Amado")
        String author,

        @Schema(description = "The book's ISBN code.", example = "9788535911695")
        String isbn,

        @Schema(description = "The list of genres associated with the book.")
        List<GenreBasicResponse> genres,

        @Schema(description = "The date and time when the book was created.", example = "2026-04-24T18:00:33")
        LocalDateTime createdAt
) {}


