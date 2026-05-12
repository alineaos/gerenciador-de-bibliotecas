package io.github.alineaos.librarymanager.dto.response;

import java.time.LocalDateTime;

public record BookPostResponse(
        Long id,
        String title,
        String author,
        String isbn,
        LocalDateTime createdAt
) {}
