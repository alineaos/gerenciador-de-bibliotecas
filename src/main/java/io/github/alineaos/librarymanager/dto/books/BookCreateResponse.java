package io.github.alineaos.librarymanager.dto.books;

import io.github.alineaos.librarymanager.dto.genres.GenreBasicResponse;

import java.time.LocalDateTime;
import java.util.List;

public record BookCreateResponse(
        Long id,
        String title,
        String author,
        String isbn,
        List<GenreBasicResponse> genres,
        LocalDateTime createdAt
) {}


