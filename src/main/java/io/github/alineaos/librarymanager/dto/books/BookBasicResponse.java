package io.github.alineaos.librarymanager.dto.books;

public record BookBasicResponse(
        Long id,
        String title,
        String author
) {}
