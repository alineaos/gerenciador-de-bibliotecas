package io.github.alineaos.librarymanager.dto.response;

public record BookBasicResponse(
        Long id,
        String title,
        String author
) {}
