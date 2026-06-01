package io.github.alineaos.librarymanager.dto.genres;

import java.time.LocalDateTime;

public record GenreInfoResponse(
        Long id,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
